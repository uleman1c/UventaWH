package com.example.uventawh;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcceptanceListViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    private MutableLiveData<Boolean> bNotLoaded,
            bNew,
            bToExecute,
            bInWork,
            bAccepted,
            bControlled,
            bMarked,
            bSelected,
            bFinished,
            bCanceled,
            bBlocked;

    public AcceptanceListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        bNotLoaded = new MutableLiveData<>();
        bNotLoaded.setValue(false);

        bNew = new MutableLiveData<>();
        bNew.setValue(false);

        bToExecute = new MutableLiveData<>();
        bToExecute.setValue(false);

        bInWork = new MutableLiveData<>();
        bInWork.setValue(false);

        bAccepted = new MutableLiveData<>();
        bAccepted.setValue(false);

        bControlled = new MutableLiveData<>();
        bControlled.setValue(false);

        bMarked = new MutableLiveData<>();
        bMarked.setValue(false);

        bSelected = new MutableLiveData<>();
        bSelected.setValue(false);

        bFinished = new MutableLiveData<>();
        bFinished.setValue(false);

        bCanceled = new MutableLiveData<>();
        bCanceled.setValue(false);

        bBlocked = new MutableLiveData<>();
        bBlocked.setValue(false);

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Boolean> getNotLoaded() {
        return bNotLoaded;
    }

    public void setNotLoaded(Boolean b) {
        bNotLoaded.setValue(b);
    }

    public LiveData<Boolean> getNew() {
        return bNew;
    }

    public void setNew(Boolean b) {
        bNew.setValue(b);
    }

    public LiveData<Boolean> getToExecute() {
        return bToExecute;
    }

    public void setToExecute(Boolean b) {
        bToExecute.setValue(b);
    }

    public LiveData<Boolean> getInWork() {
        return bInWork;
    }

    public void setInWork(Boolean b) {
        bInWork.setValue(b);
    }

    public LiveData<Boolean> getAccepted() {
        return bAccepted;
    }

    public void setAccepted(Boolean b) {
        bAccepted.setValue(b);
    }

    public LiveData<Boolean> getControlled() {
        return bControlled;
    }

    public void setControlled(Boolean b) {
        bControlled.setValue(b);
    }

    public LiveData<Boolean> getMarked() {
        return bMarked;
    }

    public void setMarked(Boolean b) {
        bMarked.setValue(b);
    }

    public LiveData<Boolean> getSelected() {
        return bSelected;
    }

    public void setSelected(Boolean b) {
        bSelected.setValue(b);
    }

    public LiveData<Boolean> getFinished() {
        return bFinished;
    }

    public void setFinished(Boolean b) {
        bFinished.setValue(b);
    }

    public LiveData<Boolean> getCanceled() {
        return bCanceled;
    }

    public void setCanceled(Boolean b) {
        bCanceled.setValue(b);
    }

    public LiveData<Boolean> getBlocked() {
        return bBlocked;
    }

    public void setBlocked(Boolean b) {
        bCanceled.setValue(b);
    }

}