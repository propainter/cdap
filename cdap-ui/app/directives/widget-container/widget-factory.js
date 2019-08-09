/*
 * Copyright © 2015 Cask Data, Inc.
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

angular.module(PKG.name + '.commons')
  .service('WidgetFactory', function() {
    this.registry = {
      'number': {
        element: '<number></number>',
        attributes: {
          'value': 'model',
          'disabled': 'disabled',
          'on-change': 'onChange',
          'is-field-required': 'isFieldRequired'
        }
      },
      'textbox': {
        element: '<text-box></text-box>',
        attributes: {
          'value': 'model',
          'on-change': 'onChange',
          placeholder: 'myconfig["widget-attributes"].placeholder'
        }
      },
      'textarea': {
        element: '<code-editor></code-editor>',
        attributes: {
          'value': 'model',
          'mode': '"plain_text"',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'rows': '{{myconfig["widget-attributes"].rows}}',
        }
      },
      'password': {
        element: '<my-password></my-password>',
        attributes: {
          'ng-model': 'model',
          'ng-trim': 'false'
        }
      },
      'datetime': {
        element: '<my-timestamp-picker></my-timestamp-picker>',
        attributes: {
          'ng-model': 'model',
          'data-label': 'Date'
        }
      },
      'csv': {
        element: '<csv-widget></csv-widget>',
        attributes: {
          'value': 'model',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'value-placeholder': 'myconfig["widget-attributes"]["value-placeholder"]',
          'on-change': 'onChange',
          'disabled': 'disabled',
        },
      },
      'dsv': {
        element: '<csv-widget></csv-widget>',
        attributes: {
          'value': 'model',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'value-placeholder': 'myconfig["widget-attributes"]["value-placeholder"]',
          'on-change': 'onChange',
          'disabled': 'disabled',
        },
      },
      'ds-multiplevalues': {
        element: '<multiple-values-widget></multiple-values-widget>',
        attributes: {
          'value': 'model',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'placeholders': 'myconfig["widget-attributes"].placeholders',
          'values-delimiter': 'myconfig["widget-attributes"]["values-delimiter"]',
          'num-values': 'myconfig["widget-attributes"].numValues',
        }
      },
      'json-editor': {
        element: '<json-editor></json-editor>',
        attributes: {
          'value': 'model',
          'mode': '"json"',
          'on-change': 'onChange',
          'disabled': 'disabled',
        }
      },
      'javascript-editor': {
        element: '<code-editor></code-editor>',
        attributes: {
          'value': 'model',
          'mode': '"javascript"',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'rows': 25,
        }
      },
      'python-editor': {
        element: '<code-editor></code-editor>',
        attributes: {
          'value': 'model',
          'mode': '"python"',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'rows': 25,
        }
      },
      'scala-editor': {
        element: '<code-editor></code-editor>',
        attributes: {
          'value': 'model',
          'mode': '"scala"',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'rows': 25,
        }
      },
      'sql-editor': {
        element: '<code-editor></code-editor>',
        attributes: {
          'value': 'model',
          'mode': '"sql"',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'rows': 15,
        }
      },
      'schema': {
        element: '<my-schema-editor></my-schema-editor>',
        attributes: {
          'ng-model': 'model',
          'data-config': 'myconfig'
        }
      },
      'keyvalue': {
        element: '<key-value-widget></key-value-widget>',
        attributes: {
          'value': 'model',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'kv-delimiter': 'myconfig["widget-attributes"]["kv-delimiter"]',
          'key-placeholder': 'myconfig["widget-attributes"]["key-placeholder"]',
          'value-placeholder': 'myconfig["widget-attributes"]["value-placeholder"]',
          'on-change': 'onChange',
          'disabled': 'disabled',
        }
      },
      'keyvalue-encoded': {
        element: '<key-value-widget></key-value-widget>',
        attributes: {
          'value': 'model',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'kv-delimiter': 'myconfig["widget-attributes"]["kv-delimiter"]',
          'key-placeholder': 'myconfig["widget-attributes"]["key-placeholder"]',
          'value-placeholder': 'myconfig["widget-attributes"]["value-placeholder"]',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'is-encoded': true,
        }
      },
      'keyvalue-dropdown': {
        element: '<key-value-dropdown-widget></key-value-dropdown-widget>',
        attributes: {
          'value': 'model',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
          'kv-delimiter': 'myconfig["widget-attributes"]["kv-delimiter"]',
          'key-placeholder': 'myconfig["widget-attributes"]["key-placeholder"]',
          'dropdown-options': 'myconfig["widget-attributes"]["dropdownOptions"]',
          'on-change': 'onChange',
          'disabled': 'disabled',
        }
      },
      'function-dropdown-with-alias': {
        element: '<function-dropdown-alias-widget></function-dropdown-alias-widget>',
        attributes: {
          'value': 'model',
          'on-change': 'onChange',
          'disabled': 'disabled',
          'placeholders': 'myconfig["widget-attributes"].placeholders',
          'dropdown-options': 'myconfig["widget-attributes"]["dropdownOptions"]',
        }
      },
      'schedule': {
        element: '<my-schedule></my-schedule>',
        attributes: {
          'ng-model': 'model',
          'data-config': 'myconfig'
        }
      },
      'select': {
        element: '<select-dropdown></select-dropdown>',
        attributes: {
          'value': 'model || myconfig.properties.default || myconfig["widget-attributes"].default',
          'options': '(myconfig.properties.values || myconfig["widget-attributes"].values)',
          'on-change': 'onChange',
          'disabled': 'disabled',
        }
      },
      'dataset-selector': {
        element: '<dataset-selector-widget></dataset-selector-widget>',
        attributes: {
          'value': 'model',
          'on-change': 'onChange',
          'placeholder': 'myconfig["widget-attributes"].placeholder',
        },
      },
      'sql-select-fields': {
        element: '<sql-selector-widget></sql-selector-widget>',
        attributes: {
          'value': 'model',
          'input-schema': 'inputSchema',
          'on-change': 'onChange',
          'disabled': 'disabled'
        }
      },
      'join-types': {
        element: '<join-type-widget></join-type-widget>',
        attributes: {
          'value': 'model',
          'input-schema': 'inputSchema',
          'on-change': 'onChange'
        }
      },
      'sql-conditions': {
        element: '<sql-conditions-widget></sql-conditions-widget>',
        attributes: {
          'value': 'model',
          'disabled': 'disabled',
          'input-schema': 'inputSchema',
          'on-change': 'onChange'
        }
      },
      'input-field-selector': {
        element: '<input-field-dropdown></input-field-dropdown>',
        attributes: {
          'value': 'model',
          'input-schema': 'inputSchema',
          'on-change': 'onChange',
          'disabled': 'disabled'
        }
      },
      'wrangler-directives': {
        element: '<wrangler-editor></wrangler-editor>',
        attributes: {
          'value': 'model',
          'disabled': 'disabled',
          'data-config': 'myconfig',
          'properties': 'properties',
          'on-change': 'onChange'
        }
      },
      'rules-engine-editor': {
        element: '<my-rules-engine-editor></my-rules-engine-editor>',
        attributes: {
          'ng-model': 'model',
          'data-config': 'myconfig',
          'properties': 'properties'
        }
      },
      'textarea-validate': {
        element: '<my-textarea-validate></my-textarea-validate>',
        attributes: {
          'ng-model': 'model',
          'config': 'myconfig',
          'disabled': 'disabled',
          'node': 'node'
        }
      },
      'multi-select': {
        element: '<multi-select></multi-select>',
        attributes: {
          'value': 'model',
          'options': 'myconfig["widget-attributes"].options',
          'on-change': 'onChange',
          'delimiter': 'myconfig["widget-attributes"].delimiter',
        }
      },
      'radio-group': {
        element: '<radio-group-widget></radio-group-widget>',
        attributes: {
          'value': 'model',
          'widget-attributes': 'myconfig["widget-attributes"]',
          'property-name':'myconfig.name',
          'on-change': 'onChange'
        }
      },
      'toggle': {
        element: '<toggle-switch-widget></toggle-switch-widget>',
        attributes: {
          'value': 'model',
          'widget-attributes': 'myconfig["widget-attributes"]',
          'disabled': 'disabled',
          'on-change': 'onChange'
        }
      },
    };
    this.registry['__default__'] = this.registry['textbox'];
  });
