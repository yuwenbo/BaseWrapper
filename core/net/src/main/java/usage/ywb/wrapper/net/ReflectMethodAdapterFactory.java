package usage.ywb.wrapper.net;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.reflect.ReflectionAccessor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 注解{@link SerializedName}在声明中有
 * <code>
 * # @Target({ElementType.FIELD, ElementType.METHOD})
 * </code>
 * 表明{@link SerializedName}可以注解在方法上，但事实上，在Gson的处理工厂类
 * {@link com.google.gson.internal.bind.ReflectiveTypeAdapterFactory}中只有对包含注解的FIELD进行JSON序列化。
 * <p>
 * 实现一个类似于ReflectiveTypeAdapterFactory的工厂类，当SerializedName注解在方法上，在类序列化时，
 * 包含注解的方法会被调用，序列化字段（对象）以参数的形式传入方法。
 * 这里只实现了反序列化过程:
 * <code>
 * void setX(Object)，
 * </code>
 * 不包含序列化:
 * <code>
 * Object getX()。
 * </code>
 *
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/6 ]
 */
public class ReflectMethodAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;
    private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
    private final Excluder excluder;

    public ReflectMethodAdapterFactory() {
        this.excluder = Excluder.DEFAULT;
        this.constructorConstructor = new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap());
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null; // it's a primitive!
        }
        Map<String, BoundMethod> map = getBoundMethods(gson, type, raw);
        if (map.size() == 0) {
            /**
             * 自定义工厂优先级高于GSON中预置工厂，其中包含对基础类型、字符串、集合等进行序列化和反序列化的实现。
             * 当对象中没有方法包含注解时，仍使用GSON预置Adapter，这里返回null。
             */
            return null;
        }
        ObjectConstructor<T> constructor = constructorConstructor.get(type);
        return new Adapter<>(constructor, getBoundFields(gson, type, raw), map);
    }

    private boolean excludeField(Field f, boolean serialize) {
        return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        Map<String, BoundField> result = new LinkedHashMap<>();
        if (raw.isInterface()) {
            return result;
        }
        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                    continue;
                }
                accessor.makeAccessible(field);
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = getFieldNames(field);
                if (fieldNames == null) continue;
                BoundField previous = null;
                for (int i = 0, size = fieldNames.size(); i < size; ++i) {
                    String name = fieldNames.get(i);
                    if (i != 0) serialize = false; // only serialize the default name
                    BoundField boundField = createBoundField(context, field, name,
                            TypeToken.get(fieldType), serialize, deserialize);
                    BoundField replaced = result.put(name, boundField);
                    if (previous == null) previous = replaced;
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                            + " declares multiple JSON fields named " + previous.name);
                }
            }
            type = TypeToken.get(Objects.requireNonNull($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass())));
            raw = type.getRawType();
        }
        return result;
    }

    private Map<String, BoundMethod> getBoundMethods(Gson context, TypeToken<?> type, Class<?> raw) {
        Map<String, BoundMethod> result = new LinkedHashMap<String, BoundMethod>();
        if (raw.isInterface()) {
            return result;
        }
        Type declaredType = type.getType();
        while (raw != Object.class) {
            Method[] methods = raw.getDeclaredMethods();
            for (Method method : methods) {
                List<String> methodNames = getMethodNames(method);
                if (methodNames == null) {
                    continue;
                }
                accessor.makeAccessible(method);
                Class<?> parameterType = method.getParameterTypes()[0];
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, parameterType);
                BoundMethod previous = null;
                for (int i = 0, size = methodNames.size(); i < size; ++i) {
                    String name = methodNames.get(i);
                    BoundMethod boundMethod = createBoundMethod(context, method, name, TypeToken.get(fieldType));
                    BoundMethod replaced = result.put(name, boundMethod);
                    if (previous == null) previous = replaced;
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                            + " declares multiple JSON fields named " + previous.name);
                }
            }
            type = TypeToken.get(Objects.requireNonNull($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass())));
            raw = type.getRawType();
        }
        return result;
    }

    private List<String> getFieldNames(Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        if (annotation != null) {
            String serializedName = annotation.value();
            String[] alternates = annotation.alternate();
            if (alternates.length == 0) {
                return Collections.singletonList(serializedName);
            }
            List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
            fieldNames.add(serializedName);
            fieldNames.addAll(Arrays.asList(alternates));
            return fieldNames;
        }
        return null;
    }

    private List<String> getMethodNames(Method method) {
        SerializedName annotation = method.getAnnotation(SerializedName.class);
        if (annotation != null) {
            Class[] parameterizedType = method.getParameterTypes();
            if (parameterizedType.length == 1) {
                String serializedName = annotation.value();
                String[] alternates = annotation.alternate();
                if (alternates.length == 0) {
                    return Collections.singletonList(serializedName);
                }
                List<String> methodNames = new ArrayList<>(alternates.length + 1);
                methodNames.add(serializedName);
                methodNames.addAll(Arrays.asList(alternates));
                return methodNames;
            }
        }
        return null;
    }

    private BoundMethod createBoundMethod(final Gson context, final Method method, final String name, final TypeToken<?> fieldType) {
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        // special casing primitives here saves ~5% on Android...
        final TypeAdapter<?> typeAdapter = context.getAdapter(fieldType);
        return new BoundMethod(name) {
            @Override
            void read(JsonReader reader, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    try {
                        method.invoke(value, fieldValue);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private BoundField createBoundField(final Gson context, final Field field, final String name, final TypeToken<?> fieldType,
                                        boolean serialize, boolean deserialize) {
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        // special casing primitives here saves ~5% on Android...
        final TypeAdapter<?> typeAdapter = context.getAdapter(fieldType);
        return new BoundField(name, serialize, deserialize) {
            @Override
            void read(JsonReader reader, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }
        };
    }

    abstract class BoundMethod {
        final String name;

        BoundMethod(String name) {
            this.name = name;
        }

        abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;
    }

    abstract class BoundField {
        final String name;
        final boolean serialized;
        final boolean deserialized;

        BoundField(String name, boolean serialized, boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }

        abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;
    }

    public final class Adapter<T> extends TypeAdapter<T> {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;
        private final Map<String, BoundMethod> boundMethods;

        Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields, Map<String, BoundMethod> boundMethods) {
            this.constructor = constructor;
            this.boundFields = boundFields;
            this.boundMethods = boundMethods;
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            T instance = constructor.construct();

            try {
                in.beginObject();
                while (in.hasNext()) {
                    boolean flag = false;
                    String name = in.nextName();
                    BoundField field = boundFields.get(name);
                    if (field != null) {
                        flag = true;
                        field.read(in, instance);
                    }
                    BoundMethod method = boundMethods.get(name);
                    //当字段和方法拥有相同注解时，字段注解的优先级高于方法注解
                    if (method != null && !flag) {
                        flag = true;
                        method.read(in, instance);
                    }
                    if (!flag) {
                        in.skipValue();
                    }
                }

            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            in.endObject();
            return instance;
        }

        @Override
        public void write(JsonWriter out, T value) {

        }
    }

}
