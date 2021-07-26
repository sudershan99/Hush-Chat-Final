package com.example.hushnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 Adapter to display various fragments in view pager
 **/
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position==0)
            return new ChatFragment();
        else if(position==1)
            return new FriendsFragment();

        else if(position==2)
            return new RequestFragment();

        else
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0)
            return "CHATS";
        else if(position==1)
            return "FRIENDS";
        else if(position==2)
            return "REQUESTS";

        else return null;
    }
}
