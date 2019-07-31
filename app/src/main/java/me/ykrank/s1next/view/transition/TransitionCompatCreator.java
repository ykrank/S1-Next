package me.ykrank.s1next.view.transition;

import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.core.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

/**
 * Created by ykrank on 2016/10/28 0028.
 */

public class TransitionCompatCreator {


    public static Transition getAutoTransition() {
        Transition autoTransitionCompat = new AutoTransition();
        Interpolator fastOutSlowIn = PathInterpolatorCompat.create(0.4f, 0, 0.2f, 1);
        autoTransitionCompat.setInterpolator(fastOutSlowIn);
        return autoTransitionCompat;
    }
}
