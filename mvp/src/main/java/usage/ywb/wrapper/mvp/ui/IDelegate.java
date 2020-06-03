package usage.ywb.wrapper.mvp.ui;

import android.app.Activity;

/**
 * 这是Activity和Fragment需要实现的基类接口，
 * 里面只是实现了一个获取Activity的方法，主要在Presenter中需要使用Context对象时调用，
 * 不直接在Presenter中创建Context对象，最大程度的防止内存泄漏
 *
 * @author yuwenbo
 * @version [ V.1.0.0  2019/3/14 ]
 */
public interface IDelegate {

    <T extends Activity> T getActivity();

}
