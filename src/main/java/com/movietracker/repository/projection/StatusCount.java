package com.movietracker.repository.projection;

import com.movietracker.domain.WatchStatus;

public interface StatusCount {
    WatchStatus getStatus();

    long getCount();
}
