package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.databinding.ItemPmRightBinding;
import me.ykrank.s1next.viewmodel.PmViewModel;

public final class PmRightAdapterDelegate extends BaseAdapterDelegate<Pm, PmRightAdapterDelegate.BindingViewHolder> {

    @Inject
    User user;

    public PmRightAdapterDelegate(Context context) {
        super(context);
        App.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    protected Class<Pm> getTClass() {
        return Pm.class;
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        Object item = items.get(position);
        if (item instanceof Pm) {
            return TextUtils.equals(((Pm) item).getAuthorId(), user.getUid());
        }
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemPmRightBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_pm_right, parent, false);
        binding.setPmViewModel(new PmViewModel());
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Pm pm, int position, @NonNull BindingViewHolder holder, @NonNull List<Object> payloads) {
        ItemPmRightBinding binding = holder.binding;
        binding.getPmViewModel().pm.set(pm);
        binding.executePendingBindings();
    }

    /**
     * make textview selectable
     *
     * @param holder
     */
    @Override
    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ItemPmRightBinding binding = ((BindingViewHolder) holder).binding;
        binding.tvMessage.setEnabled(false);
        binding.tvMessage.setEnabled(true);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemPmRightBinding binding;

        public BindingViewHolder(ItemPmRightBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
