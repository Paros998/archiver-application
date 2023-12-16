package com.pd.archiver.awsfiles.util;

import com.pd.archiver.awsfiles.entity.FileEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class FileEntityDateComparator{
    public int sortByNewest(final FileEntity f1, final FileEntity f2) {
        return f2.getCreationDate().isAfter(f1.getCreationDate())
                ? 1
                : checkIfDatesAreEqual(f1.getCreationDate(), f2.getCreationDate());
    }

    private int checkIfDatesAreEqual(final LocalDateTime date1, final LocalDateTime date2) {
        return date2.isEqual(date1) ? 0 : -1;
    }
}