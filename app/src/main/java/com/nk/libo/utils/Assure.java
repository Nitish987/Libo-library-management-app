package com.nk.libo.utils;

public interface Assure {
    <T> void accept(T result);
    void reject(String error);
}
