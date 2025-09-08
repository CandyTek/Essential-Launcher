package de.clemensbartz.android.launcher.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

public class ViewVisibilityManager {
    private static final Set<ImageView> visibleImageViews = new HashSet<>();
    private static final Object lock = new Object();
    
    public static boolean isImageViewVisible(ImageView imageView) {
        if (imageView == null) return false;
        
        // 检查是否附加到窗口
        if (imageView.getWindowToken() == null) {
            return false;
        }
        
        // 获取可见矩形
        Rect visibleRect = new Rect();
        boolean isVisible = imageView.getGlobalVisibleRect(visibleRect) && !visibleRect.isEmpty();
        
        // 如果是ListView中的item，进行更精确的检查
        if (isVisible && isInListView(imageView)) {
            isVisible = isListItemVisible(imageView);
        }
        
        // 更新可见状态
        synchronized (lock) {
            if (isVisible) {
                visibleImageViews.add(imageView);
            } else {
                visibleImageViews.remove(imageView);
            }
        }
        
        return isVisible;
    }
    
    private static boolean isInListView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent != null) {
            if (parent instanceof ListView) {
                return true;
            }
            if (parent.getParent() instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                break;
            }
        }
        return false;
    }
    
    private static boolean isListItemVisible(ImageView imageView) {
        ListView listView = findParentListView(imageView);
        if (listView == null) return true;
        
        int firstVisible = listView.getFirstVisiblePosition();
        int lastVisible = listView.getLastVisiblePosition();
        int itemPosition = getItemPositionInListView(imageView, listView);
        
        if (itemPosition == -1) return true; // 无法确定位置，假设可见
        
        // 添加缓冲区：提前1个位置开始加载，延后1个位置停止
        return itemPosition >= (firstVisible - 1) && itemPosition <= (lastVisible + 1);
    }
    
    private static ListView findParentListView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        while (parent != null) {
            if (parent instanceof ListView) {
                return (ListView) parent;
            }
            if (parent.getParent() instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                break;
            }
        }
        return null;
    }
    
    private static int getItemPositionInListView(View itemView, ListView listView) {
        // 遍历ListView的子视图找到对应的位置
        for (int i = 0; i < listView.getChildCount(); i++) {
            View child = listView.getChildAt(i);
            if (isChildOfView(itemView, child)) {
                return listView.getFirstVisiblePosition() + i;
            }
        }
        return -1;
    }
    
    private static boolean isChildOfView(View child, View parent) {
        if (child == parent) return true;
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            for (int i = 0; i < group.getChildCount(); i++) {
                if (isChildOfView(child, group.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void onImageViewRecycled(ImageView imageView) {
        synchronized (lock) {
            visibleImageViews.remove(imageView);
        }
        
        // 清除ImageView的图标，只保留默认图标
        if (imageView.getDrawable() != null) {
            // 只有当前drawable不是默认drawable时才清除
            // 这里需要根据实际情况判断什么是默认drawable
            imageView.setImageDrawable(null);
        }
    }
    
    public static int getVisibleImageViewCount() {
        synchronized (lock) {
            return visibleImageViews.size();
        }
    }
    
    public static void clearVisibilityTracking() {
        synchronized (lock) {
            visibleImageViews.clear();
        }
    }
}