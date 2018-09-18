package com.jimmy.printer.common;

import java.util.List;

public interface PrinterFinderCallback<C> {
    void onStart();

    void onFound(C c);

    void onFinished(List<C> cs);
}
