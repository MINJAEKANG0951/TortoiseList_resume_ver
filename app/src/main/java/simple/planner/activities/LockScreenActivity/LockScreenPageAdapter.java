package simple.planner.activities.LockScreenActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LockScreenPageAdapter extends FragmentStateAdapter {
    public LockScreenPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return LockScreenFragment.newInstance(false);
            case 1:
                return LockScreenFragment.newInstance(true);
        }
        return LockScreenFragment.newInstance(false);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
