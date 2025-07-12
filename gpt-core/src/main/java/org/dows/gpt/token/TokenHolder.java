package org.dows.gpt.token;

import com.alibaba.ttl.TransmittableThreadLocal;

public class TokenHolder {

    private static final ThreadLocal<String> holder = new TransmittableThreadLocal<>();

    public static String get() {
        return holder.get();
    }

    public static void set(String token) {
        holder.set(token);
    }

    public static void clear() {
        holder.remove();
    }

}
