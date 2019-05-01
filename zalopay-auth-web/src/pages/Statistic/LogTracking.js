import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Form, Card, Select, List, Tag, Icon, Row, Col, Button } from 'antd';
import { FormattedMessage } from 'umi-plugin-react/locale';

import TagSelect from '@/components/TagSelect';
import StandardFormRow from '@/components/StandardFormRow';
import LogTrackingContent from '@/components/LogTrackingContent';
import styles from './LogTracking.less';

const { Option } = Select;
const FormItem = Form.Item;

const pageSize = 10;

@connect(({ logtracking, loading, zaloUsers, services }) => ({
  logtracking,
  zaloUsers,
  services,
  loading: loading.models.logtracking,
}))
@Form.create({
  onValuesChange({ dispatch }, changedValues, allValues) {
    // eslint-disable-next-line
    console.log(changedValues, allValues);
    dispatch({
      type: 'logtracking/fetch',
      payload: {
        count: 10,
      },
    });
  },
})
class LogTracking extends Component {
  componentDidMount() {
    const { dispatch } = this.props;

    // fetch users
    // dispatch({
    //   type: 'zaloUsers/fetch',
    // });

    // fetch services
    // dispatch({
    //   type: 'services/fetch',
    //   payload: {
    //     count: 100,
    //   },
    // });

    // fetch log
    dispatch({
      type: 'logtracking/fetch',
      payload: {
        count: 10,
      },
    });
  }

  setOwner = () => {
    const { form } = this.props;
    form.setFieldsValue({
      owner: [],
    });
  };

  fetchMore = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'logtracking/appendFetch',
      payload: {
        count: pageSize,
      },
    });
  };

  render() {
    const {
      form,
      logtracking: { list },
      zaloUsers: { data },
      loading,
      services,
    } = this.props;

    const { getFieldDecorator } = form;

    const owners = data.list.map(function (user) {
      return {
        id: user.realmId,
        name : user.name,
      }
    });

    const IconText = ({ type, text }) => (
      <span>
        <Icon type={type} style={{ marginRight: 8 }} />
        {text}
      </span>
    );

    const formItemLayout = {
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 24 },
        md: { span: 12 },
      },
    };

    const actionsTextMap = {
      expandText: <FormattedMessage id="component.tagSelect.expand" defaultMessage="Expand" />,
      collapseText: (
        <FormattedMessage id="component.tagSelect.collapse" defaultMessage="Collapse" />
      ),
      selectAllText: <FormattedMessage id="component.tagSelect.all" defaultMessage="All" />,
    };

    const loadMore =
      list.length > 0 ? (
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <Button onClick={this.fetchMore} style={{ paddingLeft: 48, paddingRight: 48 }}>
            {loading ? (
              <span>
                <Icon type="loading" /> Loading...
              </span>
            ) : (
              'Load More'
            )}
          </Button>
        </div>
      ) : null;

    const listServices = [];
    if(services.list != null) {
      services.list.map(service => {
        return listServices.push(<TagSelect.Option value={service.serviceId}>{service.title}</TagSelect.Option>);
      });
    }

    return (
      <Fragment>
        <Card bordered={false}>
          <Form layout="inline">
            <StandardFormRow title="Services" block style={{ paddingBottom: 11 }}>
              <FormItem>
                {getFieldDecorator('category')(
                  <TagSelect expandable actionsText={actionsTextMap}>
                    {listServices}
                  </TagSelect>
                )}
              </FormItem>
            </StandardFormRow>
            <StandardFormRow title="User" grid>
              <Row>
                <Col>
                  <FormItem {...formItemLayout}>
                    {getFieldDecorator('owner', {
                      initialValue: [],
                    })(
                      <Select
                        mode="multiple"
                        style={{ maxWidth: 286, width: '100%' }}
                        placeholder="Select User"
                      >
                        {owners.map(owner => (
                          <Option key={owner.id} value={owner.id}>
                            {owner.name}
                          </Option>
                        ))}
                      </Select>
                    )}
                    <a className={styles.selfTrigger} onClick={this.setOwner}>
                      Reset
                    </a>
                  </FormItem>
                </Col>
              </Row>
            </StandardFormRow>
            {/*<StandardFormRow title="其它选项" grid last>*/}
              {/*<Row gutter={16}>*/}
                {/*<Col xl={8} lg={10} md={12} sm={24} xs={24}>*/}
                  {/*<FormItem {...formItemLayout} label="活跃用户">*/}
                    {/*{getFieldDecorator('user', {})(*/}
                      {/*<Select placeholder="不限" style={{ maxWidth: 200, width: '100%' }}>*/}
                        {/*<Option value="lisa">李三</Option>*/}
                      {/*</Select>*/}
                    {/*)}*/}
                  {/*</FormItem>*/}
                {/*</Col>*/}
                {/*<Col xl={8} lg={10} md={12} sm={24} xs={24}>*/}
                  {/*<FormItem {...formItemLayout} label="好评度">*/}
                    {/*{getFieldDecorator('rate', {})(*/}
                      {/*<Select placeholder="不限" style={{ maxWidth: 200, width: '100%' }}>*/}
                        {/*<Option value="good">优秀</Option>*/}
                      {/*</Select>*/}
                    {/*)}*/}
                  {/*</FormItem>*/}
                {/*</Col>*/}
              {/*</Row>*/}
            {/*</StandardFormRow>*/}
          </Form>
        </Card>
        <Card
          style={{ marginTop: 24 }}
          bordered={false}
          bodyStyle={{ padding: '8px 32px 32px 32px' }}
        >
          <List
            size="large"
            loading={list.length === 0 ? loading : false}
            rowKey="id"
            itemLayout="vertical"
            loadMore={loadMore}
            dataSource={list}
            renderItem={item => (
              <List.Item
                key={item.id}
                // actions={[
                //   <IconText type="star-o" text={item.star} />,
                //   <IconText type="like-o" text={item.like} />,
                //   <IconText type="message" text={item.message} />,
                // ]}
                extra={<div className={styles.listItemExtra} />}
              >
                <List.Item.Meta
                  title={
                    <a className={styles.listItemMetaTitle} href={item.href}>
                      {item.serviceName}
                    </a>
                  }
                  description={
                    <span>
                      <Tag>{item.requestUri}</Tag>
                    </span>
                  }
                />
                <LogTrackingContent data={item} />
              </List.Item>
            )}
          />
        </Card>
      </Fragment>
    );
  }
}

export default LogTracking;
