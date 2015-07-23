package cl.monsoon.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.databinding.ActivityOpenSourceLicenseDetailBinding;
import cl.monsoon.s1next.viewmodel.LicenseAssetViewModel;

/**
 * An Activity shows the open source license for corresponding library or file.
 */
public final class OpenSourceLicenseDetailActivity extends BaseActivity {

    private static final String EXTRA_LIBRARY_OR_FILE_NAME = "library_or_file_name";
    private static final String EXTRA_LICENSE_FILE_PATH = "license_file_path";

    public static void startOpenSourceLicenseDetailActivity(Context context, CharSequence libraryOrFileName, CharSequence licenseFilePath) {
        Intent intent = new Intent(context, OpenSourceLicenseDetailActivity.class);
        intent.putExtra(EXTRA_LIBRARY_OR_FILE_NAME, libraryOrFileName);
        intent.putExtra(EXTRA_LICENSE_FILE_PATH, licenseFilePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOpenSourceLicenseDetailBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_open_source_license_detail);

        Intent intent = getIntent();
        CharSequence libraryOrFileName = intent.getCharSequenceExtra(EXTRA_LIBRARY_OR_FILE_NAME);
        setTitle(libraryOrFileName);

        binding.setLicenseAssetViewModel(new LicenseAssetViewModel(intent.getCharSequenceExtra(
                EXTRA_LICENSE_FILE_PATH)));
    }

    @BindingAdapter("movementMethod")
    public static void setMovementMethod(TextView textView, MovementMethod movementMethod) {
        textView.setMovementMethod(movementMethod);
    }

    @BindingAdapter("filePath")
    public static void loadLicense(TextView textView, CharSequence filePath) {
        try {
            InputStream inputStream = textView.getContext().getAssets().open(filePath.toString());
            textView.setText(CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException("Can't find license.", e);
        }
    }
}
