package usage.ywb.wrapper.mvp.presenter;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import usage.ywb.wrapper.mvp.model.IBaseModel;
import usage.ywb.wrapper.mvp.ui.IBaseView;


/**
 * 所有Presenter的基类
 * <p>
 * Presenter作为连接View层和Model层桥梁，同时持有View和Model的实例
 * <p>
 * 实现{@link IBasePresenter}接口，以满足View层（context）对Presenter的依赖，同时采用弱引用的方式做了内存泄漏的预防处理。
 * Presenter通过{@link #getView()}来获取View层（context）对象
 * <p>
 * 由于Presenter调用Model层会是一个耗时的操作，View可能会在Model完成时前被回收掉，
 * 为了避免每次使用{@link #getView()}时都要做空值判断，采用动态代理的方式调用View层方法
 * <p>
 * Presenter会直接持有Model对象，在子类中每次都通过new的方式实现过于麻烦，这里采用泛型传入类型，利用反射进行实例化
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/15 ]
 */
public abstract class BasePresenter<V extends IBaseView, M extends IBaseModel> implements IBasePresenter<V> {

    /**
     * 采用弱引用来防止内存泄漏
     */
    private WeakReference<V> mViewRef;

    /**
     * View的代理引用对象
     */
    private V mProxyView;

    private M mModel;

    /**
     * 建立Presenter与View的依赖关系
     *
     * @param view UI
     */
    @Override
    @SuppressWarnings("unchecked")
    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
        //因为Presenter对View是弱引用，为防止在引用的时候View为空的情况，这里对View的引用采取动态代理的方式避免空指针异常
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (mViewRef == null || mViewRef.get() == null) {
                    return null;
                }
                return method.invoke(mViewRef.get(), args);
            }
        });
        //获取超类
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        if (superclass != null) {
            //获取泛型
            Type[] types = superclass.getActualTypeArguments();
            try {
                mModel = (M) ((Class) types[1]).newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    protected V getView() {
        return mProxyView;
    }

    protected M getModel() {
        return mModel;
    }

    @Override
    public boolean isViewAttach() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 解除Presenter与View的依赖关系
     */
    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }


}
