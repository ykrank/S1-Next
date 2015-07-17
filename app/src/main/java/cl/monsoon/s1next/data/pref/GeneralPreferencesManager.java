package cl.monsoon.s1next.data.pref;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public final class GeneralPreferencesManager {

    private final GeneralPreferencesRepository mGeneralPreferencesProvider;

    private final Supplier<Float> mTextScaleSupplier = new Supplier<Float>() {

        @Override
        public Float get() {
            return TextScale.VALUES[Integer.parseInt(mGeneralPreferencesProvider.getFontSizeString())]
                    .scale;
        }
    };
    private final Supplier<Boolean> mSignatureEnabledSupplier = new Supplier<Boolean>() {

        @Override
        public Boolean get() {
            return mGeneralPreferencesProvider.isSignatureEnabled();
        }
    };

    private volatile Supplier<Float> mTextScaleMemorized = Suppliers.memoize(mTextScaleSupplier);
    private volatile Supplier<Boolean> mSignatureEnabledMemorized = Suppliers.memoize(mSignatureEnabledSupplier);

    public GeneralPreferencesManager(GeneralPreferencesRepository generalPreferencesProvider) {
        this.mGeneralPreferencesProvider = generalPreferencesProvider;
    }

    public void invalidateTextScale() {
        mTextScaleMemorized = Suppliers.memoize(mTextScaleSupplier);
    }

    public float getTextScale() {
        return mTextScaleMemorized.get();
    }

    public void invalidateSignatureEnabled() {
        mSignatureEnabledMemorized = Suppliers.memoize(mSignatureEnabledSupplier);
    }

    public boolean isSignatureEnabled() {
        return mSignatureEnabledMemorized.get();
    }

    private enum TextScale {
        VERY_SMALL(0.8f), SMALL(0.9f), MEDIUM(1f), LARGE(1.1f), VERY_LARGE(1.2f);

        private static final TextScale[] VALUES = TextScale.values();

        private final float scale;

        TextScale(float scale) {
            this.scale = scale;
        }
    }
}
