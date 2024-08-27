import pandas as pd

# Load the Excel file
file_path = 'CMO-Historical-Data-Monthly.xlsx'
xls = pd.ExcelFile(file_path)

# Load the "Monthly Prices" sheet
monthly_prices_df = pd.read_excel(xls, sheet_name='Monthly Prices', skiprows=4)

# Set the correct column headers by combining the first two rows after skipping the initial 4 rows
new_headers = monthly_prices_df.iloc[0].fillna('') + ' ' + monthly_prices_df.iloc[1].fillna('')
new_headers = new_headers.str.strip()
monthly_prices_df.columns = new_headers

# Drop the first two rows that were used to create headers
monthly_prices_df = monthly_prices_df.drop([0, 1])

# Reset index
monthly_prices_df = monthly_prices_df.reset_index(drop=True)

# Rename the first column to 'Date'
monthly_prices_df.rename(columns={monthly_prices_df.columns[0]: 'Date'}, inplace=True)

# Convert the 'Date' column to datetime format
monthly_prices_df['Date'] = pd.to_datetime(monthly_prices_df['Date'], format='%YM%m')

# Set 'Date' as the index
monthly_prices_df.set_index('Date', inplace=True)

# Convert the data to numeric where possible
monthly_prices_df = monthly_prices_df.apply(pd.to_numeric, errors='coerce')

# Fill NaN values with the average of preceding dates
monthly_prices_df = monthly_prices_df.fillna(method='ffill').fillna(monthly_prices_df.mean())

# Drop initial rows if they start with NaN
monthly_prices_df = monthly_prices_df.dropna(how='all')

# Display the first few rows of the cleaned datafram
print(monthly_prices_df["($/mt) BARLEY"])

