package com.example.uventawh;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeliveryListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Boolean> bNotLoaded,
            bNew,
            bToPlan,
            bPlanedPartially,
            bPlaned,
            bToSelect,
            bSelecting,
            bSelected,
            bPacked,
            bReadyToShip,
            bShiped,
            bCanceled,
            bBlocked;

    public DeliveryListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        bNotLoaded = new MutableLiveData<>();
        bNotLoaded.setValue(false);

        bNew = new MutableLiveData<>();
        bNew.setValue(false);

        bToPlan = new MutableLiveData<>();
        bToPlan.setValue(false);

        bPlanedPartially = new MutableLiveData<>();
        bPlanedPartially.setValue(false);

        bPlaned = new MutableLiveData<>();
        bPlaned.setValue(false);

        bToSelect = new MutableLiveData<>();
        bToSelect.setValue(false);

        bSelecting = new MutableLiveData<>();
        bSelecting.setValue(false);

        bSelected = new MutableLiveData<>();
        bSelected.setValue(false);

        bPacked = new MutableLiveData<>();
        bPacked.setValue(false);

        bReadyToShip = new MutableLiveData<>();
        bReadyToShip.setValue(false);

        bShiped = new MutableLiveData<>();
        bShiped.setValue(false);

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

    public LiveData<Boolean> getToPlan() {
        return bToPlan;
    }

    public void setToPlan(Boolean b) {
        bToPlan.setValue(b);
    }

    public LiveData<Boolean> getPlanedPartially() {
        return bPlanedPartially;
    }

    public void setPlanedPartially(Boolean b) {
        bPlanedPartially.setValue(b);
    }

    public LiveData<Boolean> getPlaned() {
        return bPlaned;
    }

    public void setPlaned(Boolean b) {
        bPlaned.setValue(b);
    }

    public LiveData<Boolean> getToSelect() {
        return bToSelect;
    }

    public void setToSelect(Boolean b) {
        bToSelect.setValue(b);
    }

    public LiveData<Boolean> getSelecting() {
        return bSelecting;
    }

    public void setSelecting(Boolean b) {
        bSelecting.setValue(b);
    }

    public LiveData<Boolean> getSelected() {
        return bSelected;
    }

    public void setSelected(Boolean b) {
        bSelected.setValue(b);
    }

    public LiveData<Boolean> getPacked() {
        return bPacked;
    }

    public void setPacked(Boolean b) {
        bPacked.setValue(b);
    }

    public LiveData<Boolean> getReadyToShip() {
        return bReadyToShip;
    }

    public void setReadyToShip(Boolean b) {
        bReadyToShip.setValue(b);
    }

    public LiveData<Boolean> getShiped() {
        return bShiped;
    }

    public void setShiped(Boolean b) {
        bShiped.setValue(b);
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
