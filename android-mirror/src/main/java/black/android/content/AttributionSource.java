package black.android.content;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

/**
 * Created by BlackBox on 2022/2/20.
 */
@BClassName("android.content.AttributionSource")
public interface AttributionSource {
    @BConstructor
    Object _new(int i,int i2,String str,String str2);

    @BConstructor
    Object _new(int i,String str,String str2);

    @BField
    Object mAttributionSourceState();

    @BMethod
    Object getNext();
}
