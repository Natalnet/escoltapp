package br.com.escoltapp.entityservices;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.util.BitmapUtil;
import br.com.escoltapp.util.HttpUtil;
import br.com.scoltapp.BuildConfig;

public class UserService {
    public static void create(Context context, final User user, @NonNull final OnSaveListener onSaveListener) {

        HashMap<String,byte[]> map = new HashMap<>();
        map.put("firstName",user.getFirstName().getBytes());
        map.put("lastName",user.getLastName().getBytes());
        map.put("phone",user.getPhone().getBytes());
        map.put("token",user.getToken().getBytes());
        map.put("photo",BitmapUtil.bitmapToBytes(user.getPhotoBitmap()));

        String url = BuildConfig.API_URL + "/users/" + user.getId();
        HttpUtil.POST(context, url, map, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                onSaveListener.onSuccess();
            }

            @Override
            public void onError(String message) {
                onSaveListener.onFailure();
            }
        });
    }

    public static void read(Context context, final String key, @NonNull final OnReadListener onReadListener) {
        String url = BuildConfig.API_URL + "/users/" + key;
        HttpUtil.GET(context, url, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject userJson) {
                try {
                    User user = jsonToUser(userJson.getJSONObject(key));
                    user.setId(key);
                    onReadListener.onSuccess(user);
                } catch (JSONException e) {
                    onReadListener.onFailure();
                }
            }

            @Override
            public void onError(String message) {
                onReadListener.onFailure();
            }
        });
    }

    public static void readFollowers(Context context, final String key, @NonNull final OnReadFollow onReadFollow) {
        readFollow(context,key,"followers", onReadFollow);
    }

    public static void readFollowing(Context context, String key, final OnReadFollow onReadFollow) {
        readFollow(context,key,"following", onReadFollow);
    }

    private static void readFollow(Context context, final String key, String follow, @NonNull final OnReadFollow onReadFollow) {
        String url = BuildConfig.API_URL + "/users/" + key + "/" + follow;
        HttpUtil.GET(context, url, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject objects) {
                List<User> follow = new ArrayList<>();
                Iterator<String> keys = objects.keys();
                while(keys.hasNext()) {
                    try {
                        String key = keys.next();
                        User user = jsonToUser(objects.getJSONObject(key));
                        user.setId(key);
                        follow.add(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onReadFollow.onFailure();
                    }
                }
                onReadFollow.onSuccess(follow);
            }

            @Override
            public void onError(String message) {
                onReadFollow.onFailure();
            }
        });
    }

    public static void addFollowing(Context context, String key, String phoneNumber, final OnAddFollowing onAddFollowing) {
        String url = BuildConfig.API_URL + "/users/" + key + "/addfollowing/" + phoneNumber;
        HttpUtil.GET(context, url, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject objects) {
                try {
                    String userFollowedKey = objects.keys().next();
                    User user = jsonToUser(objects.getJSONObject(userFollowedKey));
                    user.setId(userFollowedKey);
                    onAddFollowing.onSuccess(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onAddFollowing.onFailure();
                }
            }

            @Override
            public void onError(String message) {
                onAddFollowing.onFailure();
            }
        });
    }

    public static void addPlace(Context context, String key, User.Place place, final OnAddPlace onAddPlace) {
        String url = BuildConfig.API_URL + "/users/" + key + "/addplace/" + place.label + "/" + place.latitude + "/" + place.longitude;
        HttpUtil.GET(context, url, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject objects) {
                onAddPlace.onSuccess();
            }

            @Override
            public void onError(String message) {
                onAddPlace.onFailure();
            }
        });

    }

    private static User jsonToUser(JSONObject userJSON) throws JSONException {
        User user = new User();
        String firstName = userJSON.getString("firstName");
        String lastName = userJSON.getString("lastName");
        String phone = userJSON.getString("phone");
        String token = userJSON.getString("token");
        String photoUrl = userJSON.getString("photoUrl");
        ArrayList<User.Place> places = new ArrayList<>();
        if(userJSON.has("places")) {
            JSONObject placesJSON = userJSON.getJSONObject("places");
            Iterator<String> keyIterator = placesJSON.keys();
            while (keyIterator.hasNext()) {
                String keyPlace = keyIterator.next();
                String label = placesJSON.getJSONObject(keyPlace).getString("label");
                String latitude = placesJSON.getJSONObject(keyPlace).getString("latitude");
                String longitude = placesJSON.getJSONObject(keyPlace).getString("longitude");
                User.Place place = new User.Place(label, Double.parseDouble(latitude), Double.parseDouble(longitude));
                places.add(place);
            }
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setPlaces(places);
        user.setToken(token);
        user.setPhotoUrl(photoUrl);

        return user;
    }

    public interface OnSaveListener {
        void onSuccess();
        void onFailure();
    }

    public interface OnReadListener {
        void onSuccess(User user);
        void onFailure();
    }

    public interface OnReadFollow {
        void onSuccess(List<User> followers);
        void onFailure();
    }

    public interface OnAddFollowing {
        void onSuccess(User user);
        void onFailure();
    }

    public interface OnAddPlace {
        void onSuccess();
        void onFailure();
    }


}
