import React, {Fragment, PureComponent} from 'react';
import { connect } from 'dva';
import router from 'umi/router';
import Link from 'umi/link';
import {Card, Row, Col, Icon, Avatar, Tag, Divider, Spin, Input, Steps, Button, Select} from 'antd';
import GridContent from '@/components/PageHeaderWrapper/GridContent';
import styles from './ZaloUserDetail.less';

@connect(({ loading, zaloUsers }) => ({
  listLoading: loading.effects['list/fetch'],
  currentUser: zaloUsers.queryUser,
  currentUserLoading: loading.effects['zaloUsers/query'],
}))
class ZaloUserDetail extends PureComponent {
  state = {
    newTags: [],
    inputVisible: false,
    inputValue: '',
  };

  componentDidMount() {
    const {
      dispatch,
      match: {params},
    } = this.props;
    dispatch({
      type: 'zaloUsers/query',
      payload: {
        userId: params.id
      },
    });
    dispatch({
      type: 'services/fetch',
      payload: {
        count: 8,
      },
    });
  }

  onTabChange = key => {
    const { match } = this.props;
    switch (key) {
      case 'services':
        router.push(`${match.url}/services`);
        break;
      case 'applications':
        router.push(`${match.url}/logs`);
        break;
      default:
        break;
    }
  };

  showInput = () => {
    this.setState({ inputVisible: true }, () => this.input.focus());
  };

  saveInputRef = input => {
    this.input = input;
  };

  handleInputChange = e => {
    this.setState({ inputValue: e.target.value });
  };

  handleInputConfirm = () => {
    const { state } = this;
    const { inputValue } = state;
    let { newTags } = state;
    if (inputValue && newTags.filter(tag => tag.label === inputValue).length === 0) {
      newTags = [...newTags, { key: `new-${newTags.length}`, label: inputValue }];
    }
    this.setState({
      newTags,
      inputVisible: false,
      inputValue: '',
    });
  };

  render() {
    const { newTags, inputVisible, inputValue } = this.state;
    const {
      listLoading,
      currentUser,
      currentUserLoading,
      match,
      location,
      children,
    } = this.props;

    const operationTabList = [
      {
        key: 'Roles',
        tab: (
          <span>
            <span style={{ fontSize: 14 }}>User Have Roles On Services</span>
          </span>
        ),
      },
      // {
      //   key: 'Logs',
      //   tab: (
      //     <span>
      //       <span style={{ fontSize: 14}}>Logs</span>
      //     </span>
      //   ),
      // },
    ];

    const extra = (
      <div className={styles.tags}>
        {inputVisible && (
          <Input
            ref={this.saveInputRef}
            type="text"
            size="small"
            style={{width: 78}}
            value={inputValue}
            onChange={this.handleInputChange}
            onBlur={this.handleInputConfirm}
            onPressEnter={this.handleInputConfirm}
          />
        )}
        {!inputVisible && (
          <Tag
            onClick={this.showInput}
            style={{background: '#fff', borderStyle: 'dashed'}}
          >
            <Icon type="plus" />
          </Tag>
        )}
      </div>
    );

    return (
      <GridContent className={styles.userCenter}>
        <Row gutter={24}>
          <Col lg={7} md={24}>
            <Card bordered={false} style={{ marginBottom: 24 }} loading={currentUserLoading}>
              {currentUser && Object.keys(currentUser).length ? (
                <div>
                  <div className={styles.avatarHolder}>
                    <img alt="" src={currentUser.avatar} />
                    <div className={styles.name}>{currentUser.name}</div>
                    <div>{currentUser.signature}</div>
                  </div>
                  <Divider dashed />
                  <div className={styles.detail}>
                    <p>
                      {/*<i className={styles.title} />*/}
                      User Name : {currentUser.username}
                    </p>
                  </div>
                  <div className={styles.detail}>
                    <p>
                      {/*<i className={styles.email} />*/}
                      Email : {currentUser.email}
                    </p>
                  </div>
                  {/*<Divider dashed />*/}
                  {/*<div className={styles.tags}>*/}
                    {/*<div className={styles.tagsTitle}>Roles</div>*/}
                    {/*{currentUser.services.map(item => (*/}
                      {/*item.serviceRoles.map(role => (*/}
                        {/*<Tag key={role}>{role.split('___')[1]}</Tag>*/}
                      {/*))*/}
                    {/*))}*/}
                  {/*</div>*/}
                  {/*<Divider style={{ marginTop: 16 }} dashed />*/}
                  {/*<div className={styles.team}>*/}
                    {/*<div className={styles.teamTitle}>Services</div>*/}
                    {/*<Spin spinning={currentUserLoading}>*/}
                      {/*<Row gutter={36}>*/}
                        {/*{currentUser.services.map(item => (*/}
                          {/*item.serviceRoles.length ? (*/}
                            {/*<Col key={item.serviceName} lg={24} xl={12}>*/}
                              {/*<Link to={item.serviceUrl}>*/}
                                {/*<Avatar size="small" src={item.serviceLogo} />*/}
                                {/*{item.serviceName}*/}
                              {/*</Link>*/}
                            {/*</Col>*/}
                          {/*) : ('')*/}
                        {/*))}*/}
                      {/*</Row>*/}
                    {/*</Spin>*/}
                  {/*</div>*/}

                  <Divider dashed />
                  {/*<div className={styles.avatarHolder}>*/}
                    {/*{inputVisible && (*/}
                      {/*<Input*/}
                        {/*ref={this.saveInputRef}*/}
                        {/*type="text"*/}
                        {/*size="small"*/}
                        {/*style={{ width: 78 }}*/}
                        {/*value={inputValue}*/}
                        {/*onChange={this.handleInputChange}*/}
                        {/*onBlur={this.handleInputConfirm}*/}
                        {/*onPressEnter={this.handleInputConfirm}*/}
                      {/*/>*/}
                    {/*)}*/}
                    {/*{!inputVisible && (*/}
                      {/*<Button onClick={this.showInput} type="primary">Add Role To Service</Button>*/}
                    {/*)}*/}
                  {/*</div><div className={styles.avatarHolder}>*/}
                    {/*{inputVisible && (*/}
                      {/*<Input*/}
                        {/*ref={this.saveInputRef}*/}
                        {/*type="text"*/}
                        {/*size="small"*/}
                        {/*style={{ width: 78 }}*/}
                        {/*value={inputValue}*/}
                        {/*onChange={this.handleInputChange}*/}
                        {/*onBlur={this.handleInputConfirm}*/}
                        {/*onPressEnter={this.handleInputConfirm}*/}
                      {/*/>*/}
                    {/*)}*/}
                    {/*{!inputVisible && (*/}
                      {/*<Button onClick={this.showInput} type="primary">Add Role To Service</Button>*/}
                    {/*)}*/}
                  {/*</div>*/}
                </div>
              ) : (
                'loading...'
              )}
            </Card>
          </Col>
          <Col lg={17} md={24}>
            <Card
              className={styles.tabsCard}
              bordered={false}
              tabList={operationTabList}
              activeTabKey={location.pathname.replace(`${match.path}/`, '')}
              onTabChange={this.onTabChange}
              loading={listLoading}
              // extra={<a href="#">Add</a>}
            >
              {children}
            </Card>
          </Col>
        </Row>
      </GridContent>
    );
  }
}

export default ZaloUserDetail;
