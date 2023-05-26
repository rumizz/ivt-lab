# Test plans for GT4500.fireTorpedo

## Single_first_time_success

For the first time, primary should fire successfully.

## Single_first_but_primary_empty_success

First time, but primary is empty. Secondary should fire successfully.

## Single_fail_no_retry

If the fired store reports a failure, the ship does not try to fire the other one.

## All_fail

Both are not empty, and both fails. Fire should not succeed.

## All_both_are_empty_fail

Both are empty. No shots should fire, fail.
