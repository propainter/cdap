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

import * as React from 'react';
import withStyles, { WithStyles, StyleRules } from '@material-ui/core/styles/withStyles';
import { transfersCreateConnect } from 'components/Transfers/Create/context';
import { CreateTransferSteps } from 'components/Transfers/Create/Content';

const styles = (): StyleRules => {
  return {
    root: {
      padding: '30px 0',
      margin: '0 auto',
      width: '60vw',
    },
  };
};

interface IStepContentProps extends WithStyles<typeof styles> {
  activeStep: number;
}

const StepContentView: React.SFC<IStepContentProps> = ({ activeStep, classes }) => {
  const Step = CreateTransferSteps[activeStep].component;

  // don't want the padded container for the Summary view
  if (activeStep === CreateTransferSteps.length - 1) {
    return <Step />;
  }

  return (
    <div className={classes.root}>
      <Step />
    </div>
  );
};

const StyledStepContent = withStyles(styles)(StepContentView);
const StepContent = transfersCreateConnect(StyledStepContent);
export default StepContent;
