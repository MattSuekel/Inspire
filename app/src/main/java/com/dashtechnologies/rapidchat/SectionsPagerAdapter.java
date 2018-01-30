package com.dashtechnologies.rapidchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by AkshayeJH on 11/06/17.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 2:
                BlogFragment blogFragment = new BlogFragment();
                return blogFragment;

            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 2:
                return "FEED";

            case 0:
                return "CHATS";

            case 1:
                return "FRIENDS";

            default:
                return null;
        }

    }

}
