package simple.planner.activities.TODOListActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import simple.planner.R;
import simple.planner.realmObjects.Setting;
import simple.planner.realmObjects.TODOListItem;
import simple.planner.util.TimeFormatter;

public class TODOListAdapter extends RecyclerView.Adapter<TODOListAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private Realm realm;
    private Context context;
    private Setting settingValues;
    private RealmResults<TODOListItem> data;
    private List<Boolean> itemCheckedStates;

    private boolean isEditMode;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private AdditionalDataChangeListener dataChangeListener;

    private boolean item_showTitle;
    private boolean item_showContent;
    private boolean item_showCreated;
    private boolean item_showUpdated;
    private boolean item_showStatus;
    private boolean item_showScheduled;
    private boolean item_showCheck;
    private boolean list_showFromOldTONew;
    private boolean list_showCompleted;
    private boolean list_showUnCompleted;

    private String languageCode;
    private long now;

    public RealmResults<TODOListItem> getData() {
        return this.data;
    }
    public List<Boolean> getItemCheckedStates() {
        return this.itemCheckedStates;
    }
    public boolean isEditMode() {
        return this.isEditMode;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setDataChangeListener(AdditionalDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public TODOListAdapter(Context context, Realm realm) {

        this.realm   = realm;
        this.context = context;
        this.settingValues  = this.realm.where(Setting.class).findFirst();
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

    public void viewFieldSet(Setting currentSettingValues) {

        if(currentSettingValues == null) return;

        item_showTitle      =   currentSettingValues.isShowTitle();
        item_showContent    =   currentSettingValues.isShowContent();
        item_showCreated    =   currentSettingValues.isShowCreated();
        item_showUpdated    =   currentSettingValues.isShowUpdated();
        item_showStatus     =   currentSettingValues.isShowProgress();
        item_showScheduled  =   currentSettingValues.isShowScheduled();
        item_showCheck      =   currentSettingValues.isShowCheckIcon();

        list_showFromOldTONew   =   currentSettingValues.isFromOldToNew();
        list_showUnCompleted    =   currentSettingValues.isShowUncompleted();
        list_showCompleted      =   currentSettingValues.isShowCompleted();

        languageCode = currentSettingValues.getLanguageCode();
    }
    public void fetchDataWithSetting() {

        if(data!=null) { data.removeAllChangeListeners();}

        RealmQuery<TODOListItem> query = realm.where(TODOListItem.class);
        if(list_showFromOldTONew) {
            query = query.sort("created", Sort.ASCENDING);
        } else {
            query = query.sort("created", Sort.DESCENDING);
        }
        if(list_showUnCompleted && !list_showCompleted) {
            query = query.equalTo("done", false);
        }
        if(!list_showUnCompleted && list_showCompleted) {
            query = query.equalTo("done", true);
        }
        data   =   query.findAll();
        itemCheckedStates = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) { itemCheckedStates.add(false);}

        data.addChangeListener(new RealmChangeListener<RealmResults<TODOListItem>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChange(RealmResults<TODOListItem> results) {

                now = System.currentTimeMillis();

                itemCheckedStates = new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    itemCheckedStates.add(false);
                }
                notifyDataSetChanged();

                if(dataChangeListener!=null) {
                    dataChangeListener.onDataChange();
                }
            }
        });

        if(dataChangeListener!=null) {
            dataChangeListener.onDataChange();
        }

    }


    public void closeRealm()
    {
        if (this.data != null)          { this.data.removeAllChangeListeners();}
        if (this.settingValues != null ){ this.settingValues.removeAllChangeListeners();}
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TODOListItem item = data.get(position);
        boolean isChecked = itemCheckedStates.get(position);

        if (isEditMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setScaleX(0f);
            holder.checkBox.setScaleY(0f);
            holder.checkBox.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
        }
        if (!isEditMode) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        if (isChecked)  holder.checkBox.setChecked(true);
        if (!isChecked) holder.checkBox.setChecked(false);

        if (item.isDone())  {
            holder.check.setVisibility(View.VISIBLE);
            holder.status.setText(context.getResources().getString(R.string.items_status_completed));
            holder.status.setTextColor(Color.parseColor("#F44336"));
        }
        if (!item.isDone()) {
            holder.check.setVisibility(View.INVISIBLE);
            holder.status.setText(context.getResources().getString(R.string.items_status_not_yet_completed));
            holder.status.setTextColor(Color.parseColor("#4CAF50"));
        }

        holder.title.setText(item.getTitle());
        if(item.getContent() == null || item.getContent().length()==0) {
            holder.content.setText(context.getResources().getString(R.string.empty));
        } else {
            holder.content.setText(item.getContent());
        }
        holder.created.setText(TimeFormatter.toString( item.getCreated(), languageCode ));
        holder.updated.setText(TimeFormatter.toString( item.getUpdated(), languageCode ));

        if(item.getAlarm()==null) {
            holder.unScheduledContainer.setVisibility(View.VISIBLE);
            holder.scheduledContainer.setVisibility(View.GONE);
        } else {
            holder.scheduled.setText( TimeFormatter.toString( item.getAlarm().getScheduled(), languageCode ));
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

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                itemCheckedStates.set(holder.getAdapterPosition(), isChecked);
            }
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        CheckBox checkBox;
        TextView title;
        TextView content;
        LinearLayout createdContainer;
        TextView created;
        LinearLayout updatedContainer;
        TextView updated;
        LinearLayout scheduledContainer;
        TextView scheduledPrefix;
        TextView scheduled;
        ImageView clock;
        LinearLayout unScheduledContainer;
        TextView status;
        ImageView check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item                    = itemView.findViewById(R.id.item);
            checkBox                = itemView.findViewById(R.id.checkbox);
            title                   = itemView.findViewById(R.id.title);
            content                 = itemView.findViewById(R.id.content);
            createdContainer        = itemView.findViewById(R.id.createdContainer);
            created                 = itemView.findViewById(R.id.created);
            updatedContainer        = itemView.findViewById(R.id.updatedContainer);
            updated                 = itemView.findViewById(R.id.updated);
            scheduledContainer      = itemView.findViewById(R.id.scheduledContainer);
            clock                   = itemView.findViewById(R.id.clock);
            scheduledPrefix         = itemView.findViewById(R.id.scheduledPrefix);
            scheduled               = itemView.findViewById(R.id.scheduled);
            unScheduledContainer    = itemView.findViewById(R.id.unScheduledContainer);
            status                  = itemView.findViewById(R.id.status);
            check                   = itemView.findViewById(R.id.check);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
    public interface AdditionalDataChangeListener {
        void onDataChange();
    }



}
