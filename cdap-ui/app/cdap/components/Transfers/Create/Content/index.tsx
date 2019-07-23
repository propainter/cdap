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
import {
  TransfersCreateContext,
  defaultContext,
  Stages,
} from 'components/Transfers/Create/context';
import StepProgress from 'components/Transfers/Create/StepProgress';
import StepContent from 'components/Transfers/Create/StepContent';
import NameDescription from 'components/Transfers/Create/NameDescription';
import SourceConfig from 'components/Transfers/Create/SourceConfig';
import TargetConfig from 'components/Transfers/Create/TargetConfig';
import Summary from 'components/Transfers/Create/Summary';
import LeftPanel from 'components/Transfers/Create/LeftPanel';
import { Theme } from 'services/ThemeHelper';
import withStyles, { WithStyles, StyleRules } from '@material-ui/core/styles/withStyles';
import Source from '../PluginPicker/Source';
import Target from '../PluginPicker/Target';

const styles = (): StyleRules => {
  return {
    root: {
      display: 'flex',
      height: 'calc(100% - 50px)',
    },
  };
};

export const StageConfiguration = {
  [Stages.CONFIGURE]: {
    label: 'Configure',
    steps: [
      {
        label: 'Set a name and description',
        component: NameDescription,
      },
      {
        label: 'Choose a source',
        component: Source,
      },
      {
        label: 'Set source configs',
        component: SourceConfig,
      },
      {
        label: 'Choose a target',
        component: Target,
      },
      {
        label: 'Set target configs',
        component: TargetConfig,
      },
      {
        label: `Review and create ${Theme.featureNames.transfers.toLowerCase()}`,
        component: Summary,
      },
    ],
  },
  [Stages.ASSESSMENT]: {
    label: 'Assessment',
    steps: [
      {
        label: 'Generate report',
      },
      {
        label: 'View report',
      },
    ],
  },
  [Stages.PUBLISH]: {
    label: 'Publish',
    steps: [
      {
        label: 'View summary',
      },
    ],
  },
};

class ContentView extends React.PureComponent<WithStyles<typeof styles>, typeof defaultContext> {
  public next = () => {
    this.setState({
      activeStep: this.state.activeStep + 1,
    });
  };

  public previous = () => {
    this.setState({
      activeStep: this.state.activeStep - 1,
    });
  };

  public setActiveStep = (step) => {
    this.setState({
      activeStep: step,
    });
  };

  public setNameDescription = (name, description) => {
    this.setState({
      name,
      description,
    });
  };

  public setSource = (source, sourceConfig) => {
    this.setState({
      source,
      sourceConfig,
    });
  };

  public setTarget = (target, targetConfig) => {
    this.setState({
      target,
      targetConfig,
    });
  };

  public state = {
    ...defaultContext,
    next: this.next,
    previous: this.previous,
    setNameDescription: this.setNameDescription,
    setSource: this.setSource,
    setTarget: this.setTarget,
    setActiveStep: this.setActiveStep,
  };

  public render() {
    return (
      <TransfersCreateContext.Provider value={this.state}>
        <div className={this.props.classes.root}>
          <LeftPanel />
          {/* <StepProgress /> */}
          <StepContent />
        </div>
      </TransfersCreateContext.Provider>
    );
  }
}

const Content = withStyles(styles)(ContentView);
export default Content;
