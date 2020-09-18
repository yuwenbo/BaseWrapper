package usage.ywb.wrapper.audio.ui.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author frank.yu
 *
 * DATE:2015.05.25
 */
public class AudioPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;
    private final List<String> titles;

    /**
     *
     * @param fm
     * @param fragments
     * @param titles
     */
    public AudioPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
