package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;
import android.widget.Toast;

import me.ykrank.s1next.data.api.model.Pm;


public final class PmViewModel {

    public final ObservableField<Pm> pm = new ObservableField<>();

    public final View.OnClickListener clickListener = v -> {
        Toast.makeText(v.getContext(), pm.get().getPmId(), Toast.LENGTH_SHORT).show();
    };
}
