package me.ykrank.s1next.widget.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by ykrank on 2017/2/27.
 */

public class CacheOnlyStreamLoader implements StreamModelLoader<String> {

    @Override
    public DataFetcher<InputStream> getResourceFetcher(final String model, int width, int height) {
        return new DataFetcher<InputStream>() {
            @Override
            public InputStream loadData(Priority priority) throws Exception {
                return null;
            }

            @Override
            public void cleanup() {

            }

            @Override
            public String getId() {
                return model;
            }

            @Override
            public void cancel() {

            }
        };
    }
}
