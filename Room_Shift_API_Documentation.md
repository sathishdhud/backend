# Room Shift API Documentation

## Endpoint
```
PUT /api/rooms/shift
```

## Description
Shift a guest from their current room to a new room in the hotel management system.

## Request Body
```json
{
  "currentRoomId": "string",
  "newRoomId": "string",
  "folioNo": "string",
  "remarks": "string"
}
```

## Request Fields
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| currentRoomId | String | Yes | Current room ID where the guest is staying |
| newRoomId | String | Yes | New room ID where the guest will be moved |
| folioNo | String | Yes | Folio number of the guest's check-in |
| remarks | String | No | Additional remarks or notes about the room shift |

## Response
```json
{
  "success": true,
  "message": "Guest successfully shifted from room [currentRoomId] to room [newRoomId]",
  "data": null
}
```

## Example Request
```json
{
  "currentRoomId": "R101",
  "newRoomId": "R205",
  "folioNo": "F20250920001",
  "remarks": "Guest requested room change due to noise issues"
}
```

## Example Response
```json
{
  "success": true,
  "message": "Guest successfully shifted from room R101 to room R205",
  "data": null
}
```

## Error Response
```json
{
  "success": false,
  "message": "Failed to shift guest to new room: [error message]",
  "data": null
}
```

## Usage
To use this endpoint, make a PUT request with the required JSON payload:
```javascript
apiClient.put('/api/rooms/shift', data)
```

## Validation Rules
1. Both current and new rooms must exist in the system
2. Current room must be occupied (status OD or OI)
3. New room must be available (status VR)
4. Check-in record must exist and be associated with the current room