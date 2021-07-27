package com.blackbox.roadways.utils;

import android.app.Activity;
import android.util.Log;

import com.blackbox.roadways.application.OrsacApplication;
import com.blackbox.roadways.camerarotation.CameraRotation;
import com.blackbox.roadways.utilclasses.dbobject.MstRoadDetails;
import com.blackbox.roadways.utilclasses.dbobject.RoadDetailsImages;
import com.blackbox.roadways.utilclasses.dbobject.SpecialFeatureImages;
import com.blackbox.roadways.utilclasses.dbobject.SpecialFeatures;
import com.blackbox.roadways.utilclasses.response.WebResponse;
import com.blackbox.roadways.utilsretrofit.GetDataService;
import com.blackbox.roadways.utilsretrofit.RetrofitClientInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterNewArray {
    private List<SpecialFeatures> masterSF;


    public void register(
            String jsonElements,
            String assetObjeect,
            Activity activity) {

        try {
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData().create(GetDataService.class);
            RequestBody reqjson_data = RequestBody.create(MediaType.parse("multipart/form-data"), jsonElements);
            Gson gson = new Gson();
            Type type = new TypeToken<MstRoadDetails>() {
            }.getType();
            MstRoadDetails cpdwaList = gson.fromJson(assetObjeect, type);
            List<MultipartBody.Part> parts = new ArrayList<>();

            try {
                // =======================================================
                List<RoadDetailsImages> imageRoad = OrsacApplication.getAppDb()
                        .getRoadDetailsImagesDAO()
                        .getRoadImageWithId(cpdwaList.getRoad_id(),
                                OrsacSharePref.getUserId(activity));

                for (int j = 0; j < imageRoad.size(); j++) {
                    File file = new File(imageRoad.get(j).getImage_path());
                    RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), CameraRotation.saveBitmapToFile(file));
                    parts.add(MultipartBody.Part.createFormData("road_photo[]", file.getName(), surveyBody));
                }
                // ==============================================================================

                List<SpecialFeatureImages> imageRoadFeature = OrsacApplication.getAppDb()
                        .getSpecialFeatureImagesDAO()
                        .getSpecialFeatureImagesWithWithRoadID(cpdwaList.getRoad_id(),
                                OrsacSharePref.getUserId(activity));

                    for (int k = 0; k < imageRoadFeature.size(); k++) {
                        File file = new File(imageRoadFeature.get(k).getImage_path());
                        RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), CameraRotation.saveBitmapToFile(file));
                        parts.add(MultipartBody.Part.createFormData("special_feature_photo[]", file.getName(), surveyBody));

                }

                //========================================================================
            } catch (Exception e) {
                Log.v("e1", "" + e.toString());
            }

            Call<ResponseBody> call = service.registerUserNewArray(
                    parts,
                    reqjson_data
            );
            Response<ResponseBody> bodyResponse = call.execute();
            StaticValues.code = bodyResponse.code();
            StaticValues.mensaje = bodyResponse.message();

            if (bodyResponse != null && bodyResponse.code() == 200) {
                if (bodyResponse.message() != null) {
                    WebResponse successObj = gson.fromJson(bodyResponse
                            .body().string().trim(), WebResponse.class);

                    if(successObj.getMessage().getMessage().equalsIgnoreCase("Success")
                    || successObj.getMessage().getMessage().equalsIgnoreCase("Already Exist")) {

                        OrsacApplication.getAppDb()
                                .getMstRoadDetailsDAO()
                                .updateTripSyncStatusMSt(successObj.getMessage().getRoadId(),
                                        OrsacSharePref.getUserId(activity));
                        /* masterSF = new ArrayList<>();
                        int count=0;
                        while (count < masterSF.size()) {

                            OrsacApplication.getAppDb()
                                    .getSpecialFeatureDAO()
                                    .updateTripSyncStatusSpecial(masterSF.get(count).getRoad_id(),
                                            OrsacSharePref.getUserId(activity));
                            count++;
                        }*/
                    }
                }
            }
            ResponseBody errorBody = bodyResponse.errorBody();
            Log.v("error22", errorBody.string());
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("error2212", e.toString());
        }
    }

}
