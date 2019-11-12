package com.kenzohenri.chatapp.Fragments;

import com.kenzohenri.chatapp.Notifications.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAT9nxj1c:APA91bFpx99yS33fgykRsc_vlZmWUbhhZ3im0wG4BJPYBejVsV0JDH2i3gzXzyYnrKsyls2OIW8aWn85c-eP5qXn_74LCUILfiLMLYf3nuqMHucTy0cthz0kFtaOPX7cKwukEQuC-sp0"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
