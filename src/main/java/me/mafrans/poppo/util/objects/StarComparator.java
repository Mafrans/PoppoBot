package me.mafrans.poppo.util.objects;

import me.mafrans.poppo.util.config.DataUser;

import java.util.Comparator;

public class StarComparator implements Comparator<DataUser> {
    @Override
    public int compare(DataUser o1, DataUser o2) {
        return o2.getStars() - o1.getStars();
    }
}
