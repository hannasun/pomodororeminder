package com.hannasun.pomodororeminder.interfaces;

/**
 *  Notify the ViewHolder itself that its  itemView was selected.
 */
public interface ItemTouchHelperViewHolder {

    /**
     * When view was selected.
     */
    void onItemSelected();

    /**
     * When view was removed.
     */
    void onItemClear();

}
