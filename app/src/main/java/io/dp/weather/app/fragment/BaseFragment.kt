package io.dp.weather.app.fragment

import android.os.Bundle
import com.trello.rxlifecycle.components.support.RxFragment
import io.dp.weather.app.activity.BaseActivityComponent
import io.dp.weather.app.activity.HasComponent

/**
 * Created by dp on 08/10/14.
 */
public abstract class BaseFragment : RxFragment() {

    public lateinit var component: BaseActivityComponent

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = getActivity() as HasComponent<BaseActivityComponent>
        this.component = activity.getComponent()
    }
}
