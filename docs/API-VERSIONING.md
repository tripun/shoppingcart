# API Versioning Documentation

## Overview
The Shopping Cart API implements versioning through URL paths and headers. All API endpoints are prefixed with `/api/v1/` to indicate the major version.

## Version Information
- Current Version: 1.0
- Minimum Supported Version: 1.0
- Base URL Format: `/api/v1/*`

## Headers
The API uses the following headers for version management:

### Request Headers
- `X-API-Version`: (Optional) Client can specify desired API version
  - Example: `X-API-Version: 1.0`

### Response Headers
- `X-API-Version`: Current API version
- `X-API-Deprecated`: Set to "true" if using a deprecated version
- `Sunset`: ISO date when deprecated version will be removed
- `Accept-Version`: Range of supported versions
- `Link`: Documentation link for deprecated versions

## Versioning Rules
1. All API endpoints must be accessed through `/api/v1/*`
2. Version numbers follow semantic versioning (MAJOR.MINOR)
3. Breaking changes require a new major version
4. Backward compatible changes use minor version updates

## Error Handling
- Invalid version number: 400 Bad Request
- Unsupported version: 400 Bad Request
- Deprecated version: Warning headers included

## Migration Guide
When migrating between versions:
1. Check the `X-API-Deprecated` header in responses
2. Note the `Sunset` date for deprecated versions
3. Follow the deprecation documentation link in the `Link` header
4. Test with new version using `X-API-Version` header

## Example Usage
```http
GET /api/v1/cart/1
X-API-Version: 1.0

HTTP/1.1 200 OK
X-API-Version: 1.0
Accept-Version: 1.0 - 1.0
Content-Type: application/json
```
