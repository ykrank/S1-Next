package me.ykrank.s1next.widget.span;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * Created by ykrank on 2016/12/30.
 */

public abstract class AttrsTagHandler implements Html.TagHandler {
    @Override
    public final void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

    }

    public abstract void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader, Attributes attributes);
}
