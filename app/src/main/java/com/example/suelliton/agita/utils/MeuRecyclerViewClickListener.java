package com.example.suelliton.agita.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MeuRecyclerViewClickListener implements RecyclerView.OnItemTouchListener {


    OnItemClickListener myListener;
    GestureDetector myGestureDetector;

    public interface OnItemClickListener {
        void OnItemClick(View view, int i);
        void OnItemLongClick(View view, int i);

        void onItemClick(View view, int position);
    }

    public MeuRecyclerViewClickListener(Context c, final RecyclerView view, final OnItemClickListener myListener) {
        this.myListener = myListener;
        myGestureDetector = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                super.onSingleTapUp(motionEvent);
                View childView = view.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (childView != null && myListener !=null)
                    myListener.OnItemClick(childView, view.getChildAdapterPosition(childView));

                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                super.onLongPress(motionEvent);
                View childView = view.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (childView != null && childView != null)
                    myListener.OnItemLongClick(childView, view.getChildAdapterPosition(childView));
            }

        });
    }
    /**
     *  Intercepta um evento de toque na tela.
     *  Se retornar falso o evento também deverá ser tratado pela View que está por baixo.
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        myGestureDetector.onTouchEvent(e);
        return false;
    }

    //Chamado quando um toque na View é detectado
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    //Chamado quando uma View filha não quer que os eventos sejam interceptados.
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
