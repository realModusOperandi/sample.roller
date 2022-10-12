import React, { useEffect, useState } from 'react';
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Column,
  Form,
  Grid,
  Select,
  SelectItem,
  Stack,
  TextInput,
  ToastNotification,
} from '@carbon/react';
import axios from 'axios';
import DiceTable from './DiceTable';

const props = {
  tabs: {
    selected: 0,
    triggerHref: '#',
    role: 'navigation',
  },
  tab: {
    href: '#',
    role: 'presentation',
    tabIndex: 0,
  },
};

const headers = [
  {
    key: 'roll',
    header: 'Roll'
  },
  {
    key: 'type',
    header: 'Dice Type'
  },
  {
    key: 'quantity',
    header: 'Dice Quantity'
  },
  {
    key: 'results',
    header: 'Roll Result'
  },
];



const LandingPage = () => {
  const [toastProps, setToastProps] = useState([]);
  const [rollResults, setRollResults] = useState([]);

  const [rollType, setRollType] = useState('d4');
  const [rollQuantity, setRolLQuantity] = useState(1);

  const arePropsEqual = (a, b) => {
    // Create arrays of property names
    let aProps = Object.getOwnPropertyNames(a);
    let bProps = Object.getOwnPropertyNames(b);

    // If number of properties is different,
    // objects are not equivalent
    if (aProps.length !== bProps.length) {
      return false;
    }

    for (let i = 0; i < aProps.length; i++) {
      let propName = aProps[i];

      // If values of same property are not equal,
      // objects are not equivalent
      if (a[propName] !== b[propName]) {
        return false;
      }
    }

    // If we made it this far, objects
    // are considered equivalent
    return true;
  };

  const showAndHideToast = async (props) => {
    showToast(props);
    await new Promise((resolve) => setTimeout(resolve, 5000));
    hideToast(props);
  };

  const showToast = (props) => {
    setToastProps(currentProps => currentProps.concat([props]));
  };

  const hideToast = (props) => {
    setToastProps(currentProps => currentProps.filter(prop => !arePropsEqual(prop, props)));
  };

  const doRoll = async (type, quantity) => {
    const body = {
      type: type,
      quantity: quantity
    };

    const response = await axios.post(`${window.location.origin}/roller-ui/rest/dice/roll`, body);
    const result = response.data;

    setRollResults(currentResults => currentResults.concat([result]));

    const notificationProps = {
      title: 'Result',
      caption: `Roll ${result.roll}: ${result.results}`,
      kind: 'success'
    }

    await showAndHideToast(notificationProps);
  }

  const renderToast = () => {
    return (
      <div>
        {toastProps.map((prop) => (
          <ToastNotification {...prop} />
        ))}
      </div>
    )
  };

  return (
    <>
      <Grid className="landing-page" fullWidth>
        <Column lg={16} md={8} sm={4} className="landing-page__banner">
          <Breadcrumb noTrailingSlash aria-label="Page navigation">
            <BreadcrumbItem>
              <a href="/">Main Page</a>
            </BreadcrumbItem>
          </Breadcrumb>
          <h1 className="landing-page__heading">
            Roll The Dice
          </h1>
        </Column>
        <Column lg={16} md={8} sm={4} className="landing-page__r2">
          <Grid className="tabs-group-content">
            <Column
              md={4}
              lg={7}
              sm={4}
              className="landing-page__tab-content">
              <p className="landing-page__p">
                Choose a die type and specify how many of that type to roll.
              </p>
              <div className="landing-page__form">
                <Form>
                  <Stack>
                      <Stack orientation="horizontal" gap={6}>
                      <Select id="dice-type" onChange={e => setRollType(e.target.value)}>
                        <SelectItem text="4-sided die" value="d4"/>
                        <SelectItem text="6-sided die" value="d6"/>
                        <SelectItem text="8-sided die" value="d8"/>
                        <SelectItem text="10-sided die" value="d10"/>
                        <SelectItem text="12-sided die" value="d12"/>
                        <SelectItem text="20-sided die" value="d20"/>
                      </Select>
                      <TextInput id="dice-quantity" labelText="Quantity" defaultValue={1} onChange={e => setRolLQuantity(parseInt(e.target.value))}/>
                      </Stack>
                      <Button onClick={() => doRoll(rollType, rollQuantity)}>Roll dice</Button>
                  </Stack>
                </Form>
              </div>
            </Column>
            <Column md={4} lg={{span: 8, offset: 8}} sm={4}>
              <DiceTable
                headers={headers}
                rows={rollResults}
              />
            </Column>
          </Grid>
        </Column>
      </Grid>
      <div className="landing-page__toast">
        {renderToast()}
      </div>

    </>
  );
};

export default LandingPage;
