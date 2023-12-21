package com.pd.archiver.awsfiles.util;

import com.pd.archiver.awsfiles.entity.FileEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * The type File entity date comparator.
 */
@UtilityClass
public class FileEntityDateComparator{
    /**
     * Sort by newest int.
     *
     * @param f1 the f 1
     * @param f2 the f 2
     * @return the int
     */
    public int sortByNewest(final FileEntity f1, final FileEntity f2) {
        return f2.getCreationDate().isAfter(f1.getCreationDate())
                ? 1
                : checkIfDatesAreEqual(f1.getCreationDate(), f2.getCreationDate());
    }

    private int checkIfDatesAreEqual(final LocalDateTime date1, final LocalDateTime date2) {
        return date2.isEqual(date1) ? 0 : -1;
    }
}