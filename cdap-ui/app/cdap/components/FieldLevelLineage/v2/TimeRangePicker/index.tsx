/*
 * Copyright © 2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
*/

import React, { useContext } from 'react';
import withStyles, { StyleRules } from '@material-ui/core/styles/withStyles';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import T from 'i18n-react';
import { TIME_OPTIONS } from 'components/FieldLevelLineage/store/Store';
import ExpandableTimeRange from 'components/TimeRangePicker/ExpandableTimeRange';
import { IContextState, FllContext } from 'components/FieldLevelLineage/v2/Context/FllContext';

const PREFIX = 'features.FieldLevelLineage.v2.TimeRangePicker';

// These styles came from components/TimeRangePicker/TimeRangePicker.scss
const styles = (): StyleRules => {
  return {
    view: {
      padding: 10,
    },
    timeRangeContainer: {
      display: 'inline-block',
      position: 'relative',
      marginLeft: 10,
      width: 400,
    },
    timePickerContainer: {
      paddingLeft: 50,
    },
    // To do: Style menu (this isn't working)
    menu: {
      getContentAnchorEl: 'null',
      anchorOrigin: {
        vertical: 'bottom',
        horizontal: 'center',
      },
      // transformOrigin: {
      //   vertical: 'top',
      //   horizontal: 'center',
      // },
    },
  };
};

function TimeRangePicker({ classes }) {
  const { start, end, selection, setTimeRange, setCustomTimeRange } = useContext<IContextState>(
    FllContext
  );

  const onSelect = (e: React.ChangeEvent<{ value: string }>) => {
    const range = e.target.value;
    setTimeRange(range);

    // render date range picker if selection is custom
    if (range === TIME_OPTIONS[0]) {
      renderCustomTimeRange();
    }
  };

  const renderCustomTimeRange = () => {
    if (selection !== TIME_OPTIONS[0]) {
      return null;
    }
    return (
      <div className={classes.timeRangeContainer}>
        <ExpandableTimeRange onDone={setCustomTimeRange} inSeconds={true} start={start} end={end} />
      </div>
    );
  };

  return (
    <div className={classes.timePickerContainer}>
      <span className={classes.view}>{T.translate(`${PREFIX}.view`)}</span>
      <Select value={selection} onChange={onSelect}>
        {TIME_OPTIONS.map((option) => {
          return (
            <MenuItem value={option} key={option}>
              {T.translate(`${PREFIX}.TimeRangeOptions.${option}`)}
            </MenuItem>
          );
        })}
      </Select>
      {renderCustomTimeRange()}
    </div>
  );
}

const StyledTimePicker = withStyles(styles)(TimeRangePicker);

export default StyledTimePicker;
