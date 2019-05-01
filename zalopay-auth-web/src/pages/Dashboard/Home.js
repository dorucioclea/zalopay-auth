import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Card, Button, Icon, List } from 'antd';

import Ellipsis from '@/components/Ellipsis';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';

import styles from './Home.less';

@connect(({ services, loading }) => ({
  services,
  loading: loading.models.services,
}))
class Home extends PureComponent {

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'services/fetch',
      payload: {
        count: 8,
      },
    });
  }

  handleAccessService(item) {
    if(item.oidc) {
      const win = window.open(`http://localhost:8000${item.path}`, '_blank');
      win.focus();
    } else {
      const win = window.open(`${item.href}`, '_blank');
      win.focus();
    }
  };

  render() {
    const {
      services: { list },
      loading,
    } = this.props;

    return (
      <PageHeaderWrapper title="Services">
        <div className={styles.cardList}>
          <List
            rowKey="id"
            loading={loading}
            grid={{ gutter: 24, lg: 3, md: 2, sm: 1, xs: 1 }}
            dataSource={[...list]}
            renderItem={item =>
              item ? (
                <List.Item key={item.id}>
                  <Card hoverable className={styles.card} actions={[<a onClick={() => this.handleAccessService(item)}>Access</a>]}>
                    <Card.Meta
                      avatar={<img alt="" className={styles.cardAvatar} src={item.avatar} />}
                      title={<a>{item.title}</a>}
                      description={
                        <Ellipsis className={styles.item} lines={3}>
                          {item.description}
                        </Ellipsis>
                      }
                    />
                  </Card>
                </List.Item>
              ) : (
                <List.Item>
                  <Button type="dashed" className={styles.newButton}>
                    <Icon type="plus" /> Send Email to SO
                  </Button>
                </List.Item>
              )
            }
          />
        </div>
      </PageHeaderWrapper>
    );
  }
}

export default Home;
