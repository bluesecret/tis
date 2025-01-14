import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { API_CONTEXT } from '../config';

export default class ReportDatasetGroupController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<ANY_OBJECT[]>(
      API_CONTEXT + '/report/reportDatasetGroup/list',
      params,
      httpOptions,
    );
  }

  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<ANY_OBJECT>(
      API_CONTEXT + '/report/reportDatasetGroup/view',
      params,
      httpOptions,
    );
  }

  //   static export(sender, params, fileName) {
  //     return super.download(API_CONTEXT + '/report/reportDatasetGroup/export', params, fileName);
  //   }

  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/report/reportDatasetGroup/add', params, httpOptions);
  }

  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/report/reportDatasetGroup/update', params, httpOptions);
  }

  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/report/reportDatasetGroup/delete', params, httpOptions);
  }

  static listAll(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<ANY_OBJECT>(
      API_CONTEXT + '/report/reportDatasetGroup/listAll',
      params,
      httpOptions,
    );
  }
}
