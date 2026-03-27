import React from 'react';
import { render, screen } from '@testing-library/react';
import ValueBox from '../components/common/sideprofile/ValueBox';

// Mock CSS module or global styles if required (optional)
// jest.mock('./ValueBox.css', () => ({}));

describe('ValueBox Component', () => {
  it('renders with "Not Active" value and class "rounded-box-2"', () => {
    render(<ValueBox value="Not Active" />);
    const valueElement = screen.getByTestId('value-box-test');
    expect(valueElement).toHaveTextContent('Not Active');
    expect(valueElement.parentElement).toHaveClass('rounded-box-2');
  });

  it('renders with "Active" value and class "rounded-box-3"', () => {
    render(<ValueBox value="Active" />);
    const valueElement = screen.getByTestId('value-box-test');
    expect(valueElement).toHaveTextContent('Active');
    expect(valueElement.parentElement).toHaveClass('rounded-box-3');
  });

  it('renders with other value and default class "rounded-box-1"', () => {
    render(<ValueBox value="Pending" />);
    const valueElement = screen.getByTestId('value-box-test');
    expect(valueElement).toHaveTextContent('Pending');
    expect(valueElement.parentElement).toHaveClass('rounded-box-1');
  });
});
