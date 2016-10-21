package me.ykrank.s1next.widget.span;

import android.net.Uri;
import android.text.method.MovementMethod;
import android.view.View;

import me.ykrank.s1next.data.api.Api;

/**
 * A movement method that provides selection and clicking on links,
 * also invokes {@link TagHandler.ImageClickableSpan}'s clicking event.
 */
public final class SearchMovementMethod extends CustomMovementMethod {
    private static SearchMovementMethod sInstance;

    protected SearchMovementMethod(){
        super(new DefaultSearchURLSpanClick());
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new SearchMovementMethod();
            sInstance.addURLSpanClick(new SarabaInsideThreadSpan());
            sInstance.addURLSpanClick(new SarabaSpan());
            sInstance.addURLSpanClick(new BilibiliSpan());
        }

        return sInstance;
    }

    public static class DefaultSearchURLSpanClick extends DefaultURLSpanClick{

        @Override
        public void onClick(Uri uri, View v) {
            if(uri.getScheme() == null){
                uri = Uri.parse(Api.BASE_URL+uri.toString());
            }
            super.onClick(uri, v);
        }
    }
}
