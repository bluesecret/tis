import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { DBLink } from '@/types/online/dblink';
import { TableInfo } from '@/types/online/table';
import { API_CONTEXT } from '../config';

export default class FlowDblinkController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<DBLink>>(
      API_CONTEXT + '/flow/flowDblink/list',
      params,
      httpOptions,
    );
  }

  static listDblinkTables(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<TableInfo[]>(
      API_CONTEXT + '/flow/flowDblink/listDblinkTables',
      params,
      httpOptions,
    );
  }

  static listDblinkTableColumns(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<ANY_OBJECT[]>(
      API_CONTEXT + '/flow/flowDblink/listDblinkTableColumns',
      params,
      httpOptions,
    );
  }

  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<DBLink>(API_CONTEXT + '/flow/flowDblink/view', params, httpOptions);
  }

  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/flow/flowDblink/add', params, httpOptions);
  }

  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/flow/flowDblink/update', params, httpOptions);
  }

  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/flow/flowDblink/delete', params, httpOptions);
  }

  static testConnection(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get(API_CONTEXT + '/flow/flowDblink/testConnection', params, httpOptions);
  }
}
