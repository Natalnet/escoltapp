package br.com.escoltapp.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private String id;

    private String firstName;

    private String lastName;

    private String phone;

    private String photoUrl;

    private Bitmap photoBitmap;

    private List<Place> places;

    private String token;

    public User() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Exclude
    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    @Exclude
    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(photoUrl);
        dest.writeList(places);
        dest.writeString(token);
        dest.writeParcelable(photoBitmap,flags);
    }

    private void readFromParcel(Parcel parcel) {
        this.id = parcel.readString();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
        this.phone = parcel.readString();
        this.photoUrl = parcel.readString();
        this.places = new ArrayList<>();
        parcel.readList(places, Place.class.getClassLoader());
        this.token = parcel.readString();
        this.photoBitmap = parcel.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel p) {
            User user = new User();
            user.readFromParcel(p);
            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Exclude
    public boolean isValid() {
        return id != null &&
                firstName != null &&
                lastName != null &&
                phone != null &&
                places != null &&
                token != null &&
                photoUrl != null;
    }

    public static class Place implements Parcelable{
        public String label;
        public Double latitude;
        public Double longitude;

        public Place() {}

        public Place(String label, double latitude, double longitude) {
            this.label = label;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(label);
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
        }

        private void readFromParcel(Parcel parcel) {
            this.label = parcel.readString();
            this.latitude = parcel.readDouble();
            this.longitude = parcel.readDouble();
        }

        public static final Creator<User.Place> CREATOR = new Creator<Place>() {
            @Override
            public Place createFromParcel(Parcel p) {
                Place place = new Place();
                place.readFromParcel(p);
                return place;
            }

            @Override
            public Place[] newArray(int size) {
                return new Place[size];
            }
        };
    }

}
