package simple.planner.activities.LockScreenActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import simple.planner.R;
import simple.planner.realmObjects.Setting;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.util.TimeFormatter;

public class LockScreenAdapter extends RecyclerView.Adapter<LockScreenAdapter.ViewHolder> {

    private Realm realm;
    private Context context;
    private Setting settingValues;

    private boolean isCompleted;
    private RealmResults<TODOListItem> items;
    private OnItemClickListener onItemClickListener;
    private OnIconClickListener onIconClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private AdditionalDataChangeListener dataChangeListener;

    private boolean item_showTitle;
    private boolean item_showContent;
    private boolean item_showCreated;
    private boolean item_showUpdated;
    private boolean item_showStatus;
    private boolean item_showScheduled;
    private boolean list_showFromOldTONew;

    private String languageCode;
    private long now;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnIconClickListener(OnIconClickListener onIconClickListener) {
        this.onIconClickListener = onIconClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setDataChangeListener(AdditionalDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public LockScreenAdapter(Context context, Realm realm, boolean isCompleted) {

        this.isCompleted = isCompleted;
        this.context = context;
        this.realm = realm;
        this.settingValues = realm.where(Setting.class).findFirst();

        viewFieldSet(settingValues);
        fetchDataWithSetting();

        this.settingValues.addChangeListener(new RealmChangeListener<Setting>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChange(Setting newSettingValues) {

                viewFieldSet(newSettingValues);
                fetchDataWithSetting();
                notifyDataSetChanged();

            }
        });

        this.now = System.currentTimeMillis();

    }

    private void viewFieldSet(Setting currentSettingValues) {

        if (currentSettingValues == null) return;

        item_showTitle = currentSettingValues.isShowTitle();
        item_showContent = currentSettingValues.isShowContent();
        item_showCreated = currentSettingValues.isShowCreated();
        item_showUpdated = currentSettingValues.isShowUpdated();
        item_showStatus = currentSettingValues.isShowProgress();
        item_showScheduled = currentSettingValues.isShowScheduled();
        list_showFromOldTONew = currentSettingValues.isFromOldToNew();
        languageCode = currentSettingValues.getLanguageCode();

    }

    private void fetchDataWithSetting() {

        if (items != null) items.removeAllChangeListeners();

        RealmQuery<TODOListItem> query = realm.where(TODOListItem.class);
        query = list_showFromOldTONew ? query.sort("created", Sort.ASCENDING) : query.sort("created", Sort.DESCENDING);
        query = query.equalTo("done", isCompleted);
        items = query.findAll();

        items.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChange(RealmResults<TODOListItem> todoListItems) {

                now = System.currentTimeMillis();

                notifyDataSetChanged();
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChange();
                }
            }
        });

        if (dataChangeListener != null) {
            dataChangeListener.onDataChange();
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lock_screen_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TODOListItem item = items.get(position);
        if (item == null) return;

        if (item.isDone())  {
            holder.status.setText(context.getResources().getString(R.string.items_status_completed));
            holder.status.setTextColor(Color.parseColor("#F44336"));
        } else {
            holder.status.setText(context.getResources().getString(R.string.items_status_not_yet_completed));
            holder.status.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.title.setText(item.getTitle());
        if(item.getContent() == null || item.getContent().length()==0) {
            holder.content.setText(context.getResources().getString(R.string.empty));
        } else {
            holder.content.setText(item.getContent());
        }
        holder.created.setText(TimeFormatter.toString( item.getCreated() , languageCode));
        holder.updated.setText(TimeFormatter.toString( item.getUpdated() , languageCode));

        if(item.getAlarm()==null) {
            holder.unScheduledContainer.setVisibility(View.VISIBLE);
            holder.scheduledContainer.setVisibility(View.GONE);
        } else {
            holder.scheduled.setText( TimeFormatter.toString( item.getAlarm().getScheduled(), languageCode));
            holder.scheduledContainer.setVisibility(View.VISIBLE);
            holder.unScheduledContainer.setVisibility(View.GONE);

            if( item.getAlarm().getScheduled() <= now ) {
                holder.clock.setImageResource(R.drawable.img_clock_disabled);
                holder.scheduledPrefix.setPaintFlags(holder.scheduledPrefix.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.scheduled.setPaintFlags(holder.scheduledPrefix.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.scheduledPrefix.setTextColor(Color.parseColor("#FF7A7979"));
                holder.scheduled.setTextColor(Color.parseColor("#FF7A7979"));
                holder.scheduledContainer.setAlpha(0.3f);
            } else {
                holder.clock.setImageResource(R.drawable.img_clock);
                holder.scheduledPrefix.setPaintFlags(holder.scheduledPrefix.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.scheduled.setPaintFlags(holder.scheduledPrefix.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.scheduledPrefix.setTextColor(Color.parseColor("#F44336"));
                holder.scheduled.setTextColor(Color.parseColor("#F44336"));
                holder.scheduledContainer.setAlpha(1f);
            }
        }

         if(isCompleted) {
            holder.checkBox.setImageResource(R.drawable.img_checkbox_selected);
        } else {
            holder.checkBox.setImageResource(R.drawable.img_checkbox);
        }

        if(item_showTitle)     { holder.title.setVisibility(View.VISIBLE); }
        if(!item_showTitle)    { holder.title.setVisibility(View.GONE); }
        if(item_showContent)   { holder.content.setVisibility(View.VISIBLE); }
        if(!item_showContent)  { holder.content.setVisibility(View.GONE); }
        if(item_showCreated)   { holder.createdContainer.setVisibility(View.VISIBLE); }
        if(!item_showCreated)  { holder.createdContainer.setVisibility(View.GONE); }
        if(item_showUpdated)   { holder.updatedContainer.setVisibility(View.VISIBLE);}
        if(!item_showUpdated)  { holder.updatedContainer.setVisibility(View.GONE);}
        if(item_showStatus)    { holder.status.setVisibility(View.VISIBLE); }
        if(!item_showStatus)   { holder.status.setVisibility(View.GONE); }
        if(!item_showScheduled) {
            holder.scheduledContainer.setVisibility(View.GONE);
            holder.unScheduledContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (items != null) return items.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item;
        private TextView title;
        private TextView content;
        private LinearLayout createdContainer;
        private TextView created;
        private LinearLayout updatedContainer;
        private TextView updated;
        private TextView status;
        private LinearLayout scheduledContainer;
        private ImageView clock;
        private TextView scheduledPrefix;
        private TextView scheduled;
        private LinearLayout unScheduledContainer;

        private ImageView checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            createdContainer = itemView.findViewById(R.id.createdContainer);
            created = itemView.findViewById(R.id.created);
            updatedContainer = itemView.findViewById(R.id.updatedContainer);
            updated = itemView.findViewById(R.id.updated);
            status = itemView.findViewById(R.id.status);
            scheduledContainer = itemView.findViewById(R.id.scheduledContainer);
            clock = itemView.findViewById(R.id.clock);
            scheduledPrefix = itemView.findViewById(R.id.scheduledPrefix);
            scheduled = itemView.findViewById(R.id.scheduled);
            unScheduledContainer = itemView.findViewById(R.id.unScheduledContainer);

            checkBox = itemView.findViewById(R.id.checkBox);

            item.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(onItemClickListener!=null) {
                    onItemClickListener.onItemClick(position);
                }
            });
            item.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if(onItemLongClickListener!=null) {
                    onItemLongClickListener.onItemLongClick(position);
                }
                return true;
            });
            checkBox.setOnClickListener(v->{
                int position = getAdapterPosition();
                if(onIconClickListener!=null) {
                    onIconClickListener.onIconClick(position);
                }
            });

        }
    }

    public void closeRealm() {
        if (items != null) {
            items.removeAllChangeListeners();
        };

        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        realm = null;
    }

    public RealmResults<TODOListItem> getItems() {
        return items;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnIconClickListener {
        void onIconClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface AdditionalDataChangeListener {
        void onDataChange();
    }

}
