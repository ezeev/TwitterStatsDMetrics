package com.wavefront;

/**
 * Created by evanpease on 1/30/16.
 */
public class PointTag {

    private String _tag;
    private String _value;

    public PointTag() {

    }

    public PointTag(String tag, String value) {
        _tag = tag;
        _value = value;
    }

    public String getTag() {
        return _tag;
    }

    public void setTag(String _tag) {
        this._tag = _tag;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String _value) {
        this._value = _value;
    }

    public String getStatStringPart() {
        return "_t_" + this._tag + "_v_" + this._value;
    }

}
