package simple.planner.realmObjects;

import java.util.Locale;

import io.realm.RealmObject;

public class Setting extends RealmObject
{
    //  TODOLIST NAME
    private String listName;

    //  EACH ITEM SETTING
    private boolean showTitle;
    private boolean showContent;
    private boolean showCreated;
    private boolean showUpdated;
    private boolean showProgress;
    private boolean showScheduled;
    private boolean showCheckIcon;

    // LIST SETTING
    private boolean fromOldToNew;
    private boolean showCompleted;
    private boolean showUncompleted;

    // LANGUAGE SETTING
    private String languageCode;

    // SERVICE SETTING
    private boolean isNotificationOn;
    private boolean isLockScreenModeOn;

    public Setting()
    {
        this.listName       = "To Do List";
        this.showTitle      = true;
        this.showContent    = true;
        this.showCreated    = true;
        this.showUpdated    = false;
        this.showProgress   = false;
        this.showScheduled  = true;
        this.showCheckIcon  = true;

        this.fromOldToNew       = false;
        this.showCompleted      = true;
        this.showUncompleted    = true;
        this.languageCode       = null;
        this.isNotificationOn   = true;
        this.isLockScreenModeOn = false;

        String languageCode = Locale.getDefault().getLanguage();
        if(languageCode.equals("ko")) {
            this.languageCode = "ko";
        }
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public boolean isShowContent() {
        return showContent;
    }

    public void setShowContent(boolean showContent) {
        this.showContent = showContent;
    }

    public boolean isShowCreated() {
        return showCreated;
    }

    public void setShowCreated(boolean showCreated) {
        this.showCreated = showCreated;
    }

    public boolean isShowUpdated() {
        return showUpdated;
    }

    public void setShowUpdated(boolean showUpdated) {
        this.showUpdated = showUpdated;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isShowScheduled() {
        return showScheduled;
    }

    public void setShowScheduled(boolean showScheduled) {
        this.showScheduled = showScheduled;
    }

    public boolean isShowCheckIcon() {
        return showCheckIcon;
    }

    public void setShowCheckIcon(boolean showCheckIcon) {
        this.showCheckIcon = showCheckIcon;
    }

    public boolean isFromOldToNew() {
        return fromOldToNew;
    }

    public void setFromOldToNew(boolean fromOldToNew) {
        this.fromOldToNew = fromOldToNew;
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
    }

    public boolean isShowUncompleted() {
        return showUncompleted;
    }

    public void setShowUncompleted(boolean showUncompleted) {
        this.showUncompleted = showUncompleted;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public boolean isNotificationOn() {
        return isNotificationOn;
    }

    public void setNotificationOn(boolean notificationOn) {
        isNotificationOn = notificationOn;
    }

    public boolean isLockScreenModeOn() {
        return isLockScreenModeOn;
    }

    public void setLockScreenModeOn(boolean lockScreenModeOn) {
        isLockScreenModeOn = lockScreenModeOn;
    }
}
