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
import { Route, Switch } from 'react-router-dom';
import Helmet from 'react-helmet';
import List from 'components/Transfers/List';
import T from 'i18n-react';
import { Theme } from 'services/ThemeHelper';
import Create from 'components/Transfers/Create';

const basepath = '/ns/:namespace/transfers';

const Transfers: React.SFC = () => {
  return (
    <div style={{ height: '100%' }}>
      <Helmet
        title={T.translate('features.Transfers.pageTitle', {
          productName: Theme.productName,
          featureName: Theme.featureNames.transfers,
        })}
      />
      <Switch>
        <Route exact path={basepath} component={List} />
        <Route exact path={`${basepath}/create`} component={Create} />
      </Switch>
    </div>
  );
};

export default Transfers;
