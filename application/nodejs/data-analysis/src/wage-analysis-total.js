/*
 * Copyright (c) 2020 Institution of Parallel and Distributed System, Shanghai Jiao Tong University
 * ServerlessBench is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
 */

function main(params) {
  console.log(JSON.stringify(params));
  console.log('[wage-analysis-total] entry');

  var stats = {'total': params['total']['statistics']['total'] };
  console.log('params[\'total\'][\'statistics\']=', JSON.stringify(params['total']['statistics']));
  console.log('stats:', JSON.stringify(stats));
  params['statistics'] = stats;
  return params;
}

exports.main = main