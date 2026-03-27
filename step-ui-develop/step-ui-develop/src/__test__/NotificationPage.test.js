import {
    render,
    screen,
    fireEvent,
    waitFor,
} from "@testing-library/react";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import { BrowserRouter } from "react-router-dom";
import rootReducer from "../../src/redux/combinedreducer";
import NotificationPage from "../components/pages/Admin_notification/NotificationPage"
import axiosInstance from "../../src/components/common/axios";
import { UuiContext, useUuiServices, StubAdaptedRouter } from "@epam/uui-core";

function UuiContextDefaultWrapper({ children }) {

    const testUuiCtx = {};
    const router = new StubAdaptedRouter();
    const { services } = useUuiServices({ router });

    const mockInitialState = {
        notifications: {},
    };

    const store = configureStore({
        reducer: rootReducer,
        preloadedState: mockInitialState,
    });

    

    Object.assign(testUuiCtx, services);

    return (
        <Provider store={store}>
            <BrowserRouter>
                <UuiContext.Provider value={services}>
                    {children}
                </UuiContext.Provider>
            </BrowserRouter>
        </Provider>
    );
}

jest.mock("axios", () => ({
    create: jest.fn(() => ({
      interceptors: {
        request: { use: jest.fn() },
        response: { use: jest.fn() },
      },
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
      patch: jest.fn(),
    })),
  }));


  

  
  async function renderToJsDom(reactElement) {
    const view = render(reactElement, { wrapper: UuiContextDefaultWrapper })
    return view;
  }

describe("NotificationPage Component Tests", () => {
    const mockData = [
        {
            uuid: "1",
            firstName: "Alice",
            lastName: "Johnson",
            practice: "Cloud",
            features: [
                { categoryId: "1", categoryName: "Email", notificationsEnabled: true },
                { categoryId: "2", categoryName: "SMS", notificationsEnabled: false },
            ],
        },
    ];

    it("renders NotificationPage and displays headers", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });

        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);

        expect(await screen.findByText("Notification Management")).toBeInTheDocument();
        expect(screen.getByPlaceholderText("Search by Name or Practice")).toBeInTheDocument();
    });


    it("renders accordion with user name", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });
        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);
        expect(await screen.findByText("Alice Johnson")).toBeInTheDocument();
    });



    it("triggers Enable/Disable All button", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });
        axiosInstance.patch.mockResolvedValue({ data: mockData[0], status: 200 });
    
        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);
        const accordionHeader = await screen.findByText("Alice Johnson");
        fireEvent.click(accordionHeader);
        const toggleAllButton = await screen.findByTestId("enable-disable-button");
        fireEvent.click(toggleAllButton);
    
        await waitFor(() =>
            expect(axiosInstance.patch).toHaveBeenCalledWith(
                `/step/notifications/1?enable=true`
            )
        );
    });
    
    it("search filters names", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });

        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);

        const searchInput = screen.getByPlaceholderText("Search by Name or Practice");
        fireEvent.change(searchInput, { target: { value: "Bob" } });

        await waitFor(() =>
            expect(screen.queryByText("Alice Johnson")).not.toBeInTheDocument()
        );
    });

    it("calls handleToggle and updates notification", async () => {
        const mockUser = {
            uuid: "1",
            firstName: "Alice",
            lastName: "Johnson",
            practice: "Cloud",
            features: [
                {
                    categoryId: "1",
                    categoryName: "Email",
                    notificationsEnabled: true
                },
                {
                    categoryId: "2",
                    categoryName: "SMS",
                    notificationsEnabled: false
                }
            ]
        };
        axiosInstance.get.mockResolvedValue({ status: 200, data: [mockUser] });
    
        const updatedUser = {
            ...mockUser,
            features: [
                {
                    categoryId: "1",
                    categoryName: "Email",
                    notificationsEnabled: false
                },
                {
                    categoryId: "2",
                    categoryName: "SMS",
                    notificationsEnabled: false
                }
            ]
        };
    
        axiosInstance.patch.mockResolvedValue({
            status: 200,
            data: updatedUser
        });
    
        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);

        const accordionHeader = await screen.findByText("Alice Johnson");
        fireEvent.click(accordionHeader);
    
     
        const toggles = await screen.findAllByTestId("toggle-button");
        const emailToggle = toggles[0];
    
        
        fireEvent.click(emailToggle);
    
        
        await waitFor(() =>
            expect(axiosInstance.patch).toHaveBeenCalledWith(
                '/step/notifications',
                JSON.stringify({
                    userId: "1",
                    categoryId: "1",
                    enable: false 
                }),
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            )
        );
    
    
        expect(screen.getByText("Notification Management")).toBeInTheDocument();
    });
    

    it("filters practice heads based on search input", async () => {
        const mockUsers = [
            {
                uuid: "1",
                firstName: "Alice",
                lastName: "Johnson",
                practice: "Cloud",
                features: []
            },
            {
                uuid: "2",
                firstName: "Bob",
                lastName: "Smith",
                practice: "Data",
                features: []
            }
        ];
    
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockUsers });
    
        await renderToJsDom(<NotificationPage closeMethod={jest.fn()} />);
    
        
        expect(await screen.findByText("Alice Johnson")).toBeInTheDocument();
        expect(screen.getByText("Bob Smith")).toBeInTheDocument();
    
        
        const searchInput = screen.getByPlaceholderText("Search by Name or Practice");
        fireEvent.change(searchInput, { target: { value: "Alice" } });
    
        
        await waitFor(() => {
            expect(screen.getByText("Alice Johnson")).toBeInTheDocument();
        });
    
        fireEvent.change(searchInput, { target: { value: "Data" } });
    
        await waitFor(() => {
            expect(screen.getByText("Bob Smith")).toBeInTheDocument();
        });
    
        fireEvent.change(searchInput, { target: { value: "NotHere" } });
    
        await waitFor(() => {
            expect(screen.queryByText("Alice Johnson")).not.toBeInTheDocument();
        });
    });
    
   
});

