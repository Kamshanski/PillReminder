package com.dawandeapp.pillreminder.ui.shared.pills;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.model.Pill;
import com.dawandeapp.pillreminder.ui.shared.BaseFragment;
import com.dawandeapp.pillreminder.utilities.M;

public class PillsFragment extends BaseFragment {

    ViewPager2 viewPager;

    public PillsFragment() {
        super();
    }

    @Override
    protected void initInstance() {
        provider = Provider.getInstance(null, null);
        provider.setPillsFragment(this);
    }

    @Override
    protected void findViews(View fv) {
        viewPager = fv.findViewById(R.id.viewPager);
    }

    @Override
    protected void initUI(View view) {
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false);
    }

    @Override
    protected void setNotifiers(View fv) {

    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_pills;
    }

    class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.PageHolder> {

        public ViewPagerAdapter() {

        }

        class PageHolder extends RecyclerView.ViewHolder {
            public PageHolder(@NonNull View itemView) {
                super(itemView);
            }


            int getActualPos() {
                int x = -((Integer.MAX_VALUE / 2) - getLayoutPosition());
                return x>=0 ? x % 4 : 3 - (Math.abs(x + 1) % 4);
            }

            void btnSchedule(String s) {
                (itemView.findViewById(R.id.btn_schedule)).setOnClickListener(v -> {
                    M.wSh(itemView.getContext(), "Вы попытались добавить расписание (пока не реализовано)");
                });
            }

            void setSchedule(String s) {
                putTextIn(s, R.id.pillSchedule);
            }

            void setName(String s) {
                putTextIn(s, R.id.tx_pillName);
            }

            void setID(String s) {
                putTextIn(s, R.id.tx_pillID);
            }

            void putTextIn(String s, int id) {
                ((TextView) itemView.findViewById(id)).setText(s);
            }
        }

        @NonNull
        @Override
        public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pill_page, parent, false);
            //M.d("viewType = " + viewType);
            return new PageHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PageHolder holder, int position) {

            //TODO: bind pill and page
            Pill pill = provider.getPills().getValue().get(holder.getActualPos());

            holder.setName(pill.getName());
            holder.setID(String.valueOf(pill.getId()));
            holder.setSchedule(pill.getSchedule().toString());

            holder.itemView.findViewById(R.id.btn_schedule)
                    .setOnClickListener(v -> M.wSh(holder.itemView.getContext(),
                            "Вы попытались добавить расписание (пока не реализовано)"));
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }
}

