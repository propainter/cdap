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

import React, { useState, useContext } from 'react';
import withStyles, { StyleRules } from '@material-ui/core/styles/withStyles';
import MenuItem from '@material-ui/core/MenuItem';
import Menu from '@material-ui/core/Menu';
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import Divider from '@material-ui/core/Divider';
import Button from '@material-ui/core/Button';
import T from 'i18n-react';
import { grey } from 'components/ThemeWrapper/colors';
import { FllContext, IContextState } from 'components/FieldLevelLineage/v2/Context/FllContext';

const PREFIX = 'features.FieldLevelLineage.v2.FllTable';
const styles = (theme): StyleRules => {
  return {
    root: {
      paddingLeft: '20px',
    },
    targetView: {
      padding: 0,
      color: theme.palette.blue[200],
      textAlign: 'right',
      textTransform: 'none',
      fontSize: 'inherit',
    },
    menu: {
      border: `1px solid ${grey[200]}`,
      borderRadius: '1px',
      color: theme.palette.blue[200],
      '& .MuiListItem-root': {
        minHeight: 0,
      },
    },
  };
};

function FllMenu({ classes }) {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const { handleViewCauseImpact } = useContext<IContextState>(FllContext);

  function handleViewClick(e: React.MouseEvent<HTMLButtonElement>) {
    setAnchorEl(e.currentTarget);
  }

  function handleClose() {
    setAnchorEl(null);
  }

  return (
    <span className={classes.root}>
      <Button onClick={handleViewClick} className={classes.targetView}>
        {T.translate(`${PREFIX}.FllField.viewDropdown`)}
        <KeyboardArrowDownIcon />
      </Button>
      <Menu
        classes={{ paper: classes.menu }}
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        getContentAnchorEl={null}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <MenuItem onClick={handleViewCauseImpact}>
          {T.translate(`${PREFIX}.FllMenu.causeImpact`)}
        </MenuItem>
        <Divider variant="middle" />
        <MenuItem onClick={handleClose}>{T.translate(`${PREFIX}.FllMenu.viewIncoming`)}</MenuItem>
        <MenuItem onClick={handleClose}>{T.translate(`${PREFIX}.FllMenu.viewOutgoing`)}</MenuItem>
      </Menu>
    </span>
  );
}

const StyledFllMenu = withStyles(styles)(FllMenu);

export default StyledFllMenu;
