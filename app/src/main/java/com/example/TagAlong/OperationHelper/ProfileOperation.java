package com.example.TagAlong.OperationHelper;


import com.example.TagAlong.Models.Profile;

public class ProfileOperation {

    private  static ProfileOperation instance = null;

    public Profile myProfile;

    public static ProfileOperation getInstance()  {
        if (instance == null)
            instance = new ProfileOperation();

        return instance;
    }

    public  ProfileOperation()
    {
        myProfile = new Profile();
    }

    public Profile  GetCurrentProfileData()
    {
        return myProfile;
    }

    public void SaveInitialData(Profile savedProfile)
    {
        myProfile.Name = savedProfile.Name;
        myProfile.Email = savedProfile.Email;
        myProfile.PhoneNumber = savedProfile.PhoneNumber;
    }

    public  void UpdateData (Profile updatedProfile)
    {
        myProfile.Gender = updatedProfile.Gender;
        myProfile.Age = updatedProfile.Age;
//        myProfile.Location = updatedProfile.Location;
        myProfile.About = updatedProfile.About;
    }


    public void SaveAllProfileData(Profile profile)
    {
        myProfile = profile;
    }

}
